package com.s8.arch.magnesium.stores.m4;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.magnesium.stores.m4.M4Store.Inspector;
import com.s8.arch.silicon.SiException;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;


/**
 * 
 * @author pierreconvert
 *
 * @param <K>
 * @param <F>
 */
public abstract class M4Node<K, F> {



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


	public M4Node(long id, Path path, long index0) {
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
	public M4Node<K, F> deserializeChildHeader(M4Store<K, F> store, ByteInflow inflow, int ithChild) throws IOException{
		long childIndex0 = index0 + ithChild*getIndexDelta();
		switch(inflow.getUInt8()) {
		case LEAF_CODE : return LeafM4Node.deserializeLeafHeader(store, inflow, childIndex0);
		case FORK_CODE : return ForkM4Node.deserializeForkHeader(store, inflow, childIndex0);
		default : throw new IOException("No matching code");
		}
	}


	public abstract void serializeHeader(ByteOutflow outflow) throws IOException;

	public abstract void saveBody(M4Store<K, F> store) throws IOException;



	public abstract boolean isLeaf();
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public abstract boolean isFull(M4Store<K, F> store) throws IOException;


	/**
	 * @param store
	 * @param value
	 * @return (if non-null) the node to apply (recursively) add method to
	 * @throws IOException 
	 * @throws SiException 
	 */
	public abstract M4Node<K, F> append(M4Store<K, F> store, F value) throws IOException, SiException;


	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public abstract M4Node<K, F> getSubNode(long index);
	

	/**
	 * 
	 * @param index
	 * @return
	 */
	public abstract F getFlow(long index);
	

	
	/**
	 * NOTA: a new stock is generated EACH time (no caching)
	 * @param store
	 * @param index
	 * @return
	 * @throws SiException
	 * @throws IOException 
	 */
	public abstract K getStock(M4Store<K, F> store, long index) throws SiException, IOException;


	/**
	 * 
	 * @param store
	 * @param consumer
	 * @throws IOException
	 */
	public abstract void traverse(M4Store<K, F> store, Inspector<F> consumer) throws IOException;
	
	
	
	public abstract long getLastIndex();

}
