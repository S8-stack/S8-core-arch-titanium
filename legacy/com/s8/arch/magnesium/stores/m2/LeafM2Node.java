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
 */
public class LeafM2Node<K> extends M2Node<K> {

	public final static byte[] OPENING_TAG = "<l:>".getBytes(StandardCharsets.US_ASCII);

	public final static byte[] CLOSING_TAG = "</l>".getBytes(StandardCharsets.US_ASCII);



	/* <body> */
	

	/**
	 * <p>BODY section</p>
	 */
	private K[] values;


	/**
	 * <p>BODY section</p>
	 */
	private int position;

	/* </body> */
	
	
	private boolean isBodyLoaded;

	public LeafM2Node(long id, Path path, long index0) {
		super(id, path, index0);
		isBodyLoaded = false;
	}





	@Override
	public int getDepth() {
		return 0; // by definition
	}	


	@Override
	public long getIndexDelta() {
		return 1; // by definition
	}

	@SuppressWarnings("unchecked")
	private void initialize(Class<K> rawType, int dimension) {
		values = (K[]) Array.newInstance(rawType, dimension);
		
	}


	public void init(Class<K> rawType, int dimension) {
		initialize(rawType, dimension);
		this.position = -1;
		isBodyLoaded = true;
	}

	@Override
	public boolean isFull(M2Store<K> store) throws IOException {
		loadBody(store);
		return values.length-1 <= position;
	}

	@Override
	public M2Node<K> add(M2Store<K> store, K value) throws IOException {
		loadBody(store);
		values[++position] = value;
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
	public static <K> M2Node<K> deserializeLeafHeader(M2Store<K> store, ByteInflow inflow, 
			int depth,
			long index0,
			long indexDelta) throws IOException{
		if(depth!=0) {
			throw new IOException("Depth must be ZERO for Leaf node!");
		}
		if(indexDelta!=1) {
			throw new IOException("Index delta must be ONE for Leaf node!");
		}
		long id = inflow.getInt64();
		return new LeafM2Node<K>(id, store.pathComposer.compose(id), index0);
	}

	@Override
	public void serializeHeader(ByteOutflow outflow) throws IOException {
		outflow.putUInt8(LEAF_CODE);
		outflow.putInt64(id);
	}


	@Override
	public void traverse(M2Store<K> store, Consumer<K> consumer) throws IOException {
		loadBody(store);
		
		for(int i=0; i<=position; i++) {
			consumer.accept(values[i]);
		}	
	}


	public void loadBody(M2Store<K> store) throws IOException {
		if(!isBodyLoaded) {

			LinkedBytes head = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(head);

			M2Prototype<K> prototype = store.prototype;
			
			initialize(prototype.getRawType(), store.dimension);
			
			if(!inflow.matches(OPENING_TAG)) {
				throw new IOException("Opneing sequence not matching");
			}
			position = inflow.getInt32();
			for(int i=0; i<=position; i++) {
				values[i] = prototype.deserialize(inflow);
			}

			if(!inflow.matches(CLOSING_TAG)) {
				throw new IOException("Clsosing sequence not matching");
			}
			isBodyLoaded = true;
		}
	}

	@Override
	public void saveBody(M2Store<K> store) throws IOException {
		if(isBodyLoaded) {
			M2Prototype<K> prototype = store.prototype;
			
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			
			outflow.putByteArray(OPENING_TAG);
			outflow.putInt32(position);
			for(int i=0; i<=position; i++) {
				prototype.serialize(values[i], outflow);
			}
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
	public M2Node<K> getSubNode(long index) {
		return null;
	}
	
	@Override
	public K get(long index) {
		int i = (int) (index - index0);
		return values[i];
	}


}
