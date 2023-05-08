package com.s8.arch.magnesium.stores.m4;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.s8.arch.magnesium.stores.m4.M4Store.Inspector;
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
 */
public class ForkM4Node<S, T> extends M4Node<S, T> {



	public final static byte[] OPENING_TAG = "<f:>".getBytes(StandardCharsets.US_ASCII);

	public final static byte[] CLOSING_TAG = "</f>".getBytes(StandardCharsets.US_ASCII);


	private final int depth;

	private final long scale;

	
	/* <body> */

	private M4Node<S, T>[] nodes;

	private int position;

	/* <body> */


	private boolean isLoaded;


	public ForkM4Node(long id, Path path, int depth, long index0, long indexDelta) {
		super(id, path, index0);
		this.depth = depth;
		this.scale = indexDelta;
		isLoaded = false;
	}


	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public long getIndexDelta() {
		return scale;
	}


	/**
	 * 
	 * @param <K>
	 * @param store
	 * @param inflow
	 * @return
	 * @throws IOException
	 */
	public static <K, F> M4Node<K, F> deserializeForkHeader(M4Store<K, F> store, 
			ByteInflow inflow, long index0) throws IOException{
		int depth = inflow.getUInt8();
		long id = inflow.getInt64();
		return new ForkM4Node<K, F>(id, store.pathComposer.compose(id), depth, index0, store.getScale(depth));
	}



	@Override
	public void serializeHeader(ByteOutflow outflow) throws IOException {
		outflow.putUInt8(FORK_CODE);
		outflow.putUInt8(depth);
		outflow.putInt64(id);
	}

	
	@SuppressWarnings("unchecked")
	private void initializeNodes(int dimension) {
		nodes = (M4Node<S, T>[]) Array.newInstance(M4Node.class, dimension);
	}
	
	/**
	 * 
	 * @param payload
	 */
	public void initialize(M4Store<S, T> store, M4Node<S, T> first) {
		initializeNodes(store.dimension);
		position = 0;
		nodes[position] = first;
		isLoaded = true;
	}



	@Override
	public boolean isFull(M4Store<S, T> store) throws IOException {

		load(store);

		if(position > (store.dimension - 1)) {
			return true;
		}
		else if(position == (nodes.length - 1)) {
			M4Node<S, T> current = nodes[position];
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
	public M4Node<S, T> append(M4Store<S, T> store, T value) throws IOException, SiException {
		load(store);
		
		M4Node<S, T> current = nodes[position];
		if(!current.isFull(store)) {
			return current;
		}
		else if(current.getDepth() < depth-1){ //current.isFull()
			M4Node<S, T> head = store.createNewForkNode(current);
			nodes[position] = head;
			return head;
		}
		else {
			position++;
			long childIndex0 = index0 + position * scale;
			M4Node<S, T> next = store.createNewLeafNode(childIndex0);
			nodes[position] = next;
			return next;
		}
	}

	
	public void load(M4Store<S, T> store) throws IOException {
		if(!isLoaded) {

			LinkedBytes head = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(head);

			initializeNodes(store.dimension);
			
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
	public void saveBody(M4Store<S, T> store) throws IOException {
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
	public void traverse(M4Store<S, T> store, Inspector<T> consumer) throws IOException {
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
	public M4Node<S, T> getSubNode(long index) {
		int i = (int) ((index - index0) / scale);
		return nodes[i];
	}

	@Override
	public T getFlow(long index) {
		return null;
	}


	@Override
	public S getStock(M4Store<S, T> prototype, long index) {
		return null;
	}


	@Override
	public long getLastIndex() {
		return nodes[position].getLastIndex();
	}
	
}
