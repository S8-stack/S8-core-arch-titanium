package com.s8.arch.magnesium.stores.m3.nodes;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Queue;
import java.util.function.BiConsumer;

import com.s8.arch.magnesium.stores.m3.ActiveList;
import com.s8.arch.magnesium.stores.m3.M3ModelPrototype;
import com.s8.arch.magnesium.stores.m3.M3Store;
import com.s8.arch.magnesium.stores.m3.requests.Action;
import com.s8.arch.magnesium.stores.m3.requests.Query;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class ForkM3Node<T> implements M3Node<T> {

	public @Override M3Node.Kind getKind() { return Kind.FORK; }


	/**
	 * 
	 */
	public final long id;

	/**
	 * 
	 */
	public final Path path;

	/**
	 * 
	 */
	public final int shift;

	/**
	 * 
	 */
	public final int mask;

	/**
	 * 
	 */
	private M3Node<T>[] buckets;


	private boolean isLoaded;

	private boolean hasBeenModifiedSinceLoading;
	
	private ActiveList<T>.NodeHandler handler;


	public ForkM3Node(long id, Path path, int capacity, int shift, int mask){
		super();

		this.id = id;
		this.path = path;
		this.shift = shift;
		this.mask = mask;

		buildBuckets(capacity);

		isLoaded = false;
	}


	@SuppressWarnings("unchecked")
	private void buildBuckets(int capacity) {
		buckets = (M3Node<T>[]) Array.newInstance(M3Node.class, capacity);
	}

	public void init(M3Store<T>	store) {
		if(!isLoaded) {
			handler = store.record(this);
			hasBeenModifiedSinceLoading = true;
			isLoaded = true;
		}
	}


	/**
	 * 
	 * @param store
	 * @param hashcode
	 * @param action
	 * @return
	 * @throws IOException
	 */
	public void descend(Query<T> query) throws IOException {

		M3Store<T> store = query.store;
		Action action = query.action;
		
		// load store
		load(store);

		// ping to signal activity on this node
		handler.signalActivity();

		int index = ((int) (query.hashcode >> shift)) & mask;
		M3Node<T> bucket = buckets[index];


		/* create new fork out of bucket for having sufficient room */
		if(bucket!=null) {

			if(action == Action.APPEND && bucket.getKind()==Kind.BUCKET && 
					((BucketM3Node<T>) bucket).size() >= store.getThreshold()) {

				ForkM3Node<T> fork = ((BucketM3Node<T>) bucket).explode(store, shift);
				buckets[index] = fork;

				// new for creation need saving
				save(store);
				hasBeenModifiedSinceLoading = true;

				
				query.setForkFollowUp(fork);
			}
			else {
				query.setFollowUp(bucket);
			}
		}
		/* create simple bucket */
		else { //i f(bucket == null) {
			if(action == Action.APPEND){
				BucketM3Node<T> bucket2 = new BucketM3Node<T>();
				buckets[index] = bucket2;

				// modified fork -> save
				hasBeenModifiedSinceLoading = true;
				//save(store);
				
				query.setBucketFollowUp(bucket2);
			}
			else {
				query.setLinkFollowUp(null); // end of the road
			}
		}
	}


	
	public void notifyChange() {
		hasBeenModifiedSinceLoading = true;
	}


	/**
	 * 
	 * @param index
	 * @param node
	 */
	void replaceBucket(int index, M3Node<T> node) {
		buckets[index] = node;
	}



	public static final int HAS_BUCKET_NEXT = 0x36;

	public static final int HAS_FORK = 0x39;

	public static final int NO_MORE_BUCKET = 0x53;



	public final static byte[] OPENING_TAG = "<f:>".getBytes(StandardCharsets.US_ASCII);

	public final static byte[] CLOSING_TAG = "</f>".getBytes(StandardCharsets.US_ASCII);



	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public void serializeHeader(ByteOutflow outflow) throws IOException {
		outflow.putInt64(id);
	}

	
	/**
	 * 
	 * @param <T>
	 * @param store
	 * @param shift
	 * @param inflow
	 * @return
	 * @throws IOException
	 */
	public static <T> ForkM3Node<T> deserializeHeader(M3Store<T> store, int shift, ByteInflow inflow) throws IOException{
		long id = inflow.getInt64();
		Path path = store.pathComposer.compose(id);
		return new ForkM3Node<T>(id, path, store.getCapacity(), shift, store.getMask());
	}


	/**
	 * 
	 * @param store
	 * @param outflow
	 * @throws IOException
	 */
	public void serializeBody(M3Store<T> store, ByteOutflow outflow) throws IOException {
		M3ModelPrototype<T> prototype = store.getPrototype();

		outflow.putByteArray(OPENING_TAG);
		int length = buckets.length;
		ForkM3Node<T> fork;
		BucketM3Node<T> bucket;
		for(int index=0; index<length; index++) {
			M3Node<T> node = buckets[index];
			if(node!=null) {
				switch(node.getKind()) {
				case BUCKET:

					bucket = (BucketM3Node<T>) node;
					if(!bucket.isEmpty()) {
						outflow.putUInt8(HAS_BUCKET_NEXT);
						outflow.putUInt16(index);
						bucket.serialize(prototype, outflow);	
					}
					break;

				case FORK:
					fork = (ForkM3Node<T>) node;
					//subForks.add(fork);
					outflow.putUInt8(HAS_FORK);
					outflow.putUInt16(index);
					fork.serializeHeader(outflow);
					break;

				default: throw new IOException("Can only be bucket of fork");
				}
			}
		}
		outflow.putUInt8(NO_MORE_BUCKET);

		outflow.putByteArray(CLOSING_TAG);
	}



	/**
	 * 
	 * @param store
	 * @param inflow
	 * @throws IOException
	 */
	public void deserializeBody(M3Store<T> store, ByteInflow inflow) throws IOException {

		M3ModelPrototype<T> prototype = store.getPrototype();

		if(!inflow.matches(OPENING_TAG)) {
			throw new IOException("Failed to match opening tag");
		}

		buildBuckets(store.getCapacity());

		int code;
		BucketM3Node<T> bucket;
		while((code = inflow.getUInt8()) != NO_MORE_BUCKET) {
			int index = inflow.getUInt16();
			switch(code) {
			case HAS_BUCKET_NEXT:
				bucket = new BucketM3Node<T>();
				bucket.deserialize(prototype, inflow);
				buckets[index] = bucket;
				break;

			case HAS_FORK:
				buckets[index] = deserializeHeader(store, shift+store.getNBits(), inflow);
				
				break;

			default: throw new IOException("tag not supported: 0x"+Integer.toHexString(code));
			}
		}

		if(!inflow.matches(CLOSING_TAG)) {
			throw new IOException("Failed to match closing tag");
		}
	}




	/**
	 * 
	 * @param store
	 * @throws IOException
	 */
	public void load(M3Store<T> store) throws IOException {
		if(!isLoaded) {
			try {
				LinkedBytes head = LinkedBytesIO.read(path, false);
				LinkedByteInflow inflow = new LinkedByteInflow(head);
				deserializeBody(store, inflow);	
			}
			catch(IOException exception) {
				exception.printStackTrace();
				init(store);
			}
			
			// handler
			handler = store.record(this);
			
			hasBeenModifiedSinceLoading = false;
			isLoaded = true;
		}
	}



	/**
	 * 
	 * @param store
	 * @throws IOException
	 */
	public void save(M3Store<T> store) throws IOException {
		if(isLoaded && hasBeenModifiedSinceLoading) {
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			serializeBody(store, outflow);
			LinkedBytes head = outflow.getHead();
			LinkedBytesIO.write(head, path, false);
			
			// extinct modification flag
			hasBeenModifiedSinceLoading = false;
		}
	}


	public boolean isLoaded() {
		return isLoaded;
	}


	/**
	 * 
	 * @param store
	 * @param queue
	 * @throws IOException
	 */
	public void dipose(M3Store<T> store, Queue<ForkM3Node<T>> queue, boolean isVerbose) throws IOException {

		if(isLoaded) {

			if(isVerbose) {
				System.out.println("Disposing node: "+path);
			}

			/**
			 * Retrieve all non-load fork-type nodes
			 */
			int length = buckets.length;
			M3Node<T> bucket;
			ForkM3Node<T> fork;
			for(int index=0; index<length; index++) {
				bucket = buckets[index];
				if(bucket != null && bucket.getKind() == Kind.FORK) {
					fork = (ForkM3Node<T>) bucket;
					if(fork.isLoaded) {
						queue.add(fork);	
					}
				}
			}

			// save (if necessary)
			save(store);

			// release handler
			handler = null;

			// release buckets
			buckets = null;

			hasBeenModifiedSinceLoading = false;
			isLoaded = false;		
		}
	}


	/**
	 * 
	 * @param consumer
	 * @param queue
	 * @throws IOException 
	 */
	public void traverse(M3Store<T> store, BiConsumer<String, T> consumer, Queue<ForkM3Node<T>> queue) throws IOException {
		load(store);
		
		int length = buckets.length;
		M3Node<T> bucket;
		for(int index=0; index<length; index++) {
			bucket = buckets[index];
			if(bucket != null) {
				switch(bucket.getKind()) {
				
				case FORK:
					queue.add((ForkM3Node<T>) bucket);
					break;
					
				case BUCKET:
					((BucketM3Node<T>) bucket).traverse(consumer);
					break;
					
				default: // do nothing
					break;
				}
			}
		}
	}
}
