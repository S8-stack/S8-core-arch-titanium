package com.s8.stack.arch.magnesium.stores.m2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;

public abstract class M2Node<K> {



	public final static int FORK_CODE = 0x17;
	public final static int LEAF_CODE = 0x28;


	/* <header> */

	public final long id;

	/* </header> */

	/**
	 * computed on the fly
	 */
	public final long index0;


	/**
	 * computed on the fly
	 */
	public final Path path;


	public M2Node(long id, Path path, long index0) {
		super();
		this.id = id;
		this.path = path;
		this.index0 = index0;
	}

	public abstract int getDepth();

	public abstract long getIndexDelta();


	/**
	 * 
	 * @param <K>
	 * @param inflow
	 * @return
	 * @throws IOException
	 */
	public M2Node<K> deserializeChildHeader(M2Store<K> store, ByteInflow inflow, int ithChild) throws IOException{

		int depth = getDepth()-1;
		long childIndex0 = index0 + ithChild*getIndexDelta();
		long childIndexDelta = getIndexDelta()/store.dimension;

		switch(inflow.getUInt8()) {
		case LEAF_CODE : return LeafM2Node.deserializeLeafHeader(store, inflow, depth, childIndex0, childIndexDelta);
		case FORK_CODE : return ForkM2Node.deserializeForkHeader(store, inflow, depth, childIndex0, childIndexDelta);
		default : throw new IOException("No mathing code");
		}
	}


	public abstract void serializeHeader(ByteOutflow outflow) throws IOException;

	public abstract void saveBody(M2Store<K> store) throws IOException;



	public abstract boolean isLeaf();
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public abstract boolean isFull(M2Store<K> store) throws IOException;


	/**
	 * @param store
	 * @param value
	 * @return (if non-null) the node to apply (recursively) add method to
	 * @throws IOException 
	 */
	public abstract M2Node<K> add(M2Store<K> store, K value) throws IOException;


	public abstract M2Node<K> getSubNode(long index);
	

	public abstract K get(long index);


	public abstract void traverse(M2Store<K> store, Consumer<K> consumer) throws IOException;

}
