package com.s8.arch.magnesium.stores.m4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import com.s8.arch.magnesium.paths.NodePathComposer;
import com.s8.arch.silicon.SiException;
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
 * @param <S>
 * @param <T>
 */
public abstract class M4Store<S, T> {


	public static final String FILENAME = "store";
	
	private static final byte[] OPENING_TAG = "<M4Store:>".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] CLOSING_TAG = "</M4Store>".getBytes(StandardCharsets.US_ASCII);

	public final NodePathComposer pathComposer;

	private final AtomicLong nodeIdGenerator;

	//public final M4Prototype<K, F> prototype;

	public M4Node<S, T> head;

	public final int dimension;

	public final Path path;

	public boolean isVerbose;

	
	
	
	private long index;
	
	/**
	 * current position of stock;
	 */
	private S state;

	private int[] scales;

	private boolean isLoaded;
	
	
	public M4Store(Path root, int dimension, boolean isVerbose) {
		super();
		this.pathComposer = new NodePathComposer(root);
		path = root.resolve(FILENAME);
		this.nodeIdGenerator = new AtomicLong();
		this.dimension = dimension;

		this.isVerbose = isVerbose;
		isLoaded = false;
	}
	
	
	
	/**
	 * 
	 * @param root
	 * @return
	 */
	public static boolean isExisting(Path root) {
		return root.resolve(FILENAME).toFile().exists();
	}

	
	public abstract LeafM4Node<S, T> createLeaf(long id, Path path, long index0);


	
	/**
	 * 
	 * @param flow
	 * @return
	 * @throws SiException 
	 */
	public abstract void operate(S stock, T flow) throws SiException;
	

	public abstract S copy(S stock) throws SiException, IOException;

	public int getScale(int depth) {
		if(depth > 0) {
			if(scales == null || depth > scales.length - 1) {
				scales = new int[depth+1];
				int scale = 1;
				scales[0] = scale;
				for(int i=1; i <= depth; i++) {
					scale = dimension * scale;
					scales[i] = scale;
				}
			}
			return scales[depth];
		}
		else {
			return 1;
		}
	}




	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws SiException 
	 */
	public LeafM4Node<S, T> createNewLeafNode(long index0) throws SiException, IOException {
		long id = nodeIdGenerator.getAndIncrement();
		if(isVerbose) {
			System.out.println("New ID: "+id);	
		}
		Path path = pathComposer.compose(id);
		LeafM4Node<S, T> node = createLeaf(id, path, index0);
		node.initBody(copy(state), dimension);
		return node;
	}


	public ForkM4Node<S, T> createNewForkNode(M4Node<S, T> first) {
		long id = nodeIdGenerator.getAndIncrement();
		if(isVerbose) {
			System.out.println("New ID: "+id);	
		}

		Path path = pathComposer.compose(id);
		ForkM4Node<S, T> node = new ForkM4Node<S, T>(id, path, 
				first.getDepth()+1, 
				first.index0, 
				first.getIndexDelta()*dimension);

		node.initialize(this, first);

		return node;
	}

	/**
	 * 
	 * @param value
	 * @throws IOException 
	 * @throws SiException 
	 */
	public void appendTransition(T value) throws IOException, SiException {
		
		if(value==null) {
			throw new SiException(0x00, "null transition");
		}
		if(head.isFull(this)) {
			head = createNewForkNode(head);
		}

		M4Node<S, T> node = head;
		while(node != null) {
			node = node.append(this, value);
		}

		// update current state
		operate(state, value);
		index++;
	}


	/**
	 * 
	 * @param index
	 * @return
	 */
	public T getTransition(long index) {
		M4Node<S, T> node = head;
		while(!node.isLeaf()) {
			node = node.getSubNode(index);
		}
		return node.getFlow(index);
	}

	
	/**
	 * 
	 * @param index
	 * @return a <b>COPY</b> of the stock for the current index
	 * @throws SiException
	 * @throws IOException 
	 */
	public S getState(long index) throws SiException, IOException {
		M4Node<S, T> node = head;
		while(!node.isLeaf()) {
			node = node.getSubNode(index);
		}
		return node.getStock(this, index);
	}



	/**
	 * 
	 * @return
	 */
	public long getCurrentIndex() {
		return index;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws SiException 
	 */
	public S getCurrentState() throws SiException, IOException {
		return copy(state);
	}
	
	
	
	public interface Inspector<T> {
		
		public void accept(long index, T transition);
	}
	
	/**
	 * 
	 * @param consumer
	 * @throws IOException
	 */
	public void traverse(Inspector<T> consumer) throws IOException {
		head.traverse(this, consumer);
	}

	
	
	/**
	 * <p>MUST call <code>initializeStore</code>.</p>
	 * @param inflow
	 * @return
	 * @throws SiException 
	 * @throws IOException 
	 */
	public abstract void initialize(long index0, S stock) throws SiException, IOException;


	/**
	 * 
	 * @param index
	 * @param state
	 * @throws IOException 
	 */
	public void initializeStore(long index, S state) throws SiException, IOException {

		this.index = index;
		this.state = copy(state);
		
		head = createNewLeafNode(index);
		nodeIdGenerator.set(0x02L);
	}


	/**
	 * <p>MUST call <code>deserializeStore</code>.</p>
	 * @param inflow
	 * @return
	 * @throws IOException 
	 */
	public abstract void deserialize(ByteInflow inflow) throws IOException;
	

	/**
	 * 		stock = prototype.deserializeStock(inflow);

	 * @param inflow
	 * @throws IOException
	 */
	public void deserializeStore(ByteInflow inflow) throws IOException {
		
		if(!inflow.matches(OPENING_TAG)) {
			throw new IOException("Opneing sequence not matching");
		}
		
		long freeId = inflow.getInt64();
		nodeIdGenerator.set(freeId);
		long index0 = inflow.getInt64();

		int code;
		switch((code = inflow.getUInt8())) {

		case M4Node.LEAF_CODE : 
			head = LeafM4Node.deserializeLeafHeader(this, inflow, index0);
			break;

		case M4Node.FORK_CODE : 
			head = ForkM4Node.deserializeForkHeader(this, inflow, index0);
			break;

		default : throw new IOException("No matching code: "+code);
		}

		
		// stock
		index = inflow.getInt64();
		state = deserializeStock(inflow);
		
		if(!inflow.matches(CLOSING_TAG)) {
			throw new IOException("Clsosing sequence not matching");
		}
	}


	
	
	/**
	 * 
	 * @param inflow
	 * @return
	 * @throws IOException 
	 */
	public abstract S deserializeStock(ByteInflow inflow) throws IOException;
	
	

	

	
	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serialize(ByteOutflow outflow) throws IOException;
	
	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public void serializeStore(ByteOutflow outflow) throws IOException {
		outflow.putByteArray(OPENING_TAG);
		outflow.putInt64(nodeIdGenerator.get());

		// one time header maths
		outflow.putInt64(head.index0);
		head.serializeHeader(outflow);
		
		// stock
		outflow.putInt64(index);
		serializeStock(state, outflow);

		outflow.putByteArray(CLOSING_TAG);	
	}
	

	public abstract void serializeStock(S stock, ByteOutflow outflow) throws IOException;
		

	
	public void boot(long index0, S stock0) throws SiException, IOException {
		if(stock0==null) {
			throw new SiException(0x00, "null transition");
		}
		initialize(index0, stock0);
		isLoaded = true;
	}
	
	

	
	/**
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		if(!isLoaded) {
			LinkedBytes bytesHead = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(bytesHead);
			deserialize(inflow);
			isLoaded = true;
		}
	}
	

	/**
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		if(isLoaded) {
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			
			serialize(outflow);
			
			LinkedBytes bytes = outflow.getHead();
			LinkedBytesIO.write(bytes, path, false);
			
			head.saveBody(this);
		}
	}

}
