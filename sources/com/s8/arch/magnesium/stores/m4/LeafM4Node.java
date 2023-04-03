package com.s8.arch.magnesium.stores.m4;

import java.io.IOException;
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
 */
public abstract class LeafM4Node<S, T> extends M4Node<S, T> {

	public final static byte[] OPENING_TAG = "<l:>".getBytes(StandardCharsets.US_ASCII);

	public final static byte[] CLOSING_TAG = "</l>".getBytes(StandardCharsets.US_ASCII);



	/* <body> */

	
	private S origin;

	/**
	 * <p>BODY section</p>
	 */
	private T[] transitions;


	/**
	 * Index in array to be used for next appending.
	 * (position == 0 => means nothing stored yet)
	 * <p>BODY section</p>
	 */
	private int position;

	/* </body> */
	
	
	private boolean isBodyLoaded;

	public LeafM4Node(long id, Path path, long index0) {
		super(id, path, index0);
		isBodyLoaded = false;
	}




	public S getStock0() {
		return origin;
	}
	
	
	@Override
	public int getDepth() {
		return 0; // by definition
	}	


	@Override
	public long getIndexDelta() {
		return 1; // by definition
	}

	/*
	@SuppressWarnings("unchecked")
	private void initializeFlows(Class<F> rawFlowType, int dimension) {
		this.values = (F[]) Array.newInstance(rawFlowType, dimension);
	}
	*/


	public void initBody(S stock, int dimension) throws SiException {
		if(stock == null) {
			throw new SiException(0x00, "null origin");
		}
		this.origin = stock;
		transitions = initializeBody(dimension);
		this.position = 0;
		isBodyLoaded = true;
	}
	
	public abstract T[] initializeBody(int dimension);

	@Override
	public boolean isFull(M4Store<S, T> store) throws IOException {
		loadBody(store);
		return transitions.length <= position;
	}

	@Override
	public M4Node<S, T> append(M4Store<S, T> store, T value) throws IOException {
		loadBody(store);
		transitions[position++] = value;
		return null;
	}


	/**
	 * 
	 * @param <K>
	 * @param store
	 * @param inflow
	 * @return
	 * @throws IOException
	 */
	public static <K, F> M4Node<K, F> deserializeLeafHeader(M4Store<K, F> store, ByteInflow inflow, 
			long index0) throws IOException{
		long id = inflow.getInt64();
		return store.createLeaf(id, store.pathComposer.compose(id), index0);
	}

	@Override
	public void serializeHeader(ByteOutflow outflow) throws IOException {
		outflow.putUInt8(LEAF_CODE);
		outflow.putInt64(id);
	}


	@Override
	public void traverse(M4Store<S, T> store, Inspector<T> consumer) throws IOException {
		loadBody(store);
		for(int i = 0; i < position; i++) {
			consumer.accept(index0 + i, transitions[i]);
		}
	}
	
	
	/** Body */
	public final static class Body<S, T> {
		
		/** state */
		public final S state;
		
		/** transitions */
		public final T[] transitions;
		
		public Body(S state, T[] transitions) {
			super();
			this.state = state;
			this.transitions = transitions;
		}		
	}
	
	
	/**
	 * 
	 * @param inflow
	 * @param length
	 * @param capacity
	 * @return
	 * @throws IOException
	 */
	public abstract Body<S, T> deserializeBody(ByteInflow inflow, int length, int capacity) throws IOException;


	/**
	 * 
	 * @param store
	 * @throws IOException
	 */
	public void loadBody(M4Store<S, T> store) throws IOException {
		if(!isBodyLoaded) {

			LinkedBytes head = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(head);
			
			if(!inflow.matches(OPENING_TAG)) {
				throw new IOException("Opening sequence not matching");
			}
			position = inflow.getInt32();
			
			// inflow
			Body<S, T> body = deserializeBody(inflow, position, store.dimension);
			origin = body.state;
			if(origin == null) {
				throw new IOException("Origin is null");
			}
			transitions = body.transitions;
			
			if(!inflow.matches(CLOSING_TAG)) {
				throw new IOException("Closing sequence not matching");
			}
			isBodyLoaded = true;
		}
	}
	
	
	/**
	 * 
	 * @param stock
	 * @param flows
	 * @param length
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serializeBody(S stock, T[] flows, int length, ByteOutflow outflow) throws IOException;
	

	@Override
	public void saveBody(M4Store<S, T> store) throws IOException {
		if(isBodyLoaded) {
			
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			
			
			outflow.putByteArray(OPENING_TAG);
			outflow.putInt32(position);
			/*
			prototype.serializeStock(origin, outflow);
			
			
			for(int i=0; i<=position; i++) {
				prototype.serializeFlow(values[i], outflow);
			}
			*/
			
			serializeBody(origin, transitions, position, outflow);

			outflow.putByteArray(CLOSING_TAG);
			LinkedBytes head = outflow.getHead();
			LinkedBytesIO.write(head, path, false);
			
			if(store.isVerbose) {
				System.out.println("SAVE LEAF: "+path);
			}
		}
	}


	@Override
	public boolean isLeaf() {
		return true;
	}


	@Override
	public M4Node<S, T> getSubNode(long index) {
		return null;
	}
	
	
	@Override
	public T getFlow(long index) {
		int i = (int) (index - index0);
		return transitions[i];
	}
	
	
	@Override
	public S getStock(M4Store<S, T> store, long index) throws SiException, IOException {
		
		// load body first if necessary
		loadBody(store);
		
		S stock = store.copy(origin);
		int nTransitions = (int) (index - index0);
		if(nTransitions > (1 + this.position)) {
			throw new IOException("Cannot access this revision");
		}
		for(int i = 0; i < nTransitions; i++) {
			store.operate(stock, transitions[i]);
		}
		return stock;
	}
	
	
	@Override
	public long getLastIndex() {
		return index0 + position - 1;
	}


}
