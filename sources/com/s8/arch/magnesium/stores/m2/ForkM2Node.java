package com.s8.arch.magnesium.stores.m2;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;

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
 * @param <K>
 */
public class ForkM2Node<K> extends M2Node<K> {



	public final static byte[] OPENING_TAG = "<f:>".getBytes(StandardCharsets.US_ASCII);

	public final static byte[] CLOSING_TAG = "</f>".getBytes(StandardCharsets.US_ASCII);


	private final int depth;

	private final long indexDelta;

	
	/* <body> */

	private M2Node<K>[] nodes;

	private int position;

	/* <body> */


	private boolean isLoaded;


	public ForkM2Node(long id, Path path, int depth, long index0, long indexDelta) {
		super(id, path, index0);
		this.depth = depth;
		this.indexDelta = indexDelta;
		isLoaded = false;
	}


	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public long getIndexDelta() {
		return indexDelta;
	}


	/**
	 * 
	 * @param <K>
	 * @param store
	 * @param inflow
	 * @return
	 * @throws IOException
	 */
	public static <K> M2Node<K> deserializeForkHeader(M2Store<K> store, 
			ByteInflow inflow, 
			int depth, 
			long index0,
			long indexDelta) throws IOException{
		long id = inflow.getInt64();
		return new ForkM2Node<K>(id, store.pathComposer.compose(id), depth, index0, indexDelta);
	}



	@Override
	public void serializeHeader(ByteOutflow outflow) throws IOException {
		outflow.putUInt8(FORK_CODE);
		outflow.putInt64(id);
	}

	
	@SuppressWarnings("unchecked")
	private void initialize(int dimension) {
		nodes = (M2Node<K>[]) Array.newInstance(M2Node.class, dimension);
	}
	
	/**
	 * 
	 * @param payload
	 */
	public void init(M2Store<K> store, M2Node<K> first) {
		initialize(store.dimension);
		nodes[0] = first;
		isLoaded = true;
	}



	@Override
	public boolean isFull(M2Store<K> store) throws IOException {

		load(store);

		if(position > (store.dimension - 1)) {
			return true;
		}
		else if(position == (nodes.length - 1)) {
			M2Node<K> current = nodes[position];
			if(current==null || current.getDepth() < (depth-1)) {
				return false;
			}
			else {
				return current.isFull(store);
			}
		}
		else {
			return false;
		}
	}

	@Override
	public M2Node<K> add(M2Store<K> store, K value) throws IOException {
		load(store);
		
		M2Node<K> current = nodes[position];
		if(!current.isFull(store)) {
			return current;
		}
		else if(current.getDepth() < depth-1){ //current.isFull()
			M2Node<K> head = store.createNewForkNode(current);
			nodes[position] = head;
			return head;
		}
		else {
			position++;
			long childIndex0 = index0 + position * indexDelta;
			M2Node<K> next = store.createNewLeafNode(childIndex0);
			nodes[position] = next;
			return next;
		}
	}

	
	public void load(M2Store<K> store) throws IOException {
		if(!isLoaded) {

			LinkedBytes head = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(head);

			initialize(store.dimension);
			
			if(!inflow.matches(OPENING_TAG)) {
				throw new IOException("Opneing sequence not matching");
			}
			position = inflow.getInt32();
			for(int i=0; i<=position; i++) {
				nodes[i] = deserializeChildHeader(store, inflow, i);
			}

			if(!inflow.matches(CLOSING_TAG)) {
				throw new IOException("Clsosing sequence not matching");
			}
			isLoaded = true;
		}
	}

	@Override
	public void saveBody(M2Store<K> store) throws IOException {
		if(isLoaded) {
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			
			outflow.putByteArray(OPENING_TAG);
			outflow.putInt32(position);
			for(int i=0; i<=position; i++) {
				nodes[i].serializeHeader(outflow);
			}
			outflow.putByteArray(CLOSING_TAG);
			LinkedBytes head = outflow.getHead();
			LinkedBytesIO.write(head, path, false);
			
			if(store.isVerbose) {
				System.out.println("SAVE FORK: "+path);
			}
			
			for(int i=0; i<=position; i++) {
				nodes[i].saveBody(store);
			}
		}
	}


	@Override
	public void traverse(M2Store<K> store, Consumer<K> consumer) throws IOException {
		load(store);
		
		for(int i=0; i<=position; i++) {
			nodes[i].traverse(store, consumer);
		}
	}


	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public M2Node<K> getSubNode(long index) {
		int i = (int) ((index - index0) / indexDelta);
		return nodes[i];
	}

	@Override
	public K get(long index) {
		return null;
	}
	
}
