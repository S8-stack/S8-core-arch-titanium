package com.s8.stack.arch.magnesium.stores.m4;

import java.io.IOException;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public interface M4ModelPrototype<K, T> {

	
	/**
	 * 
	 * @return
	 */
	public Class<K> getStockType();
	
	
	/**
	 * 
	 * @return
	 */
	public Class<T> getFlowType();
	
	
	/**
	 * 
	 * @param value
	 * @param outflow
	 * @throws IOException 
	 */
	public void serializeFlow(T value, ByteOutflow outflow) throws IOException;
	
	
	/**
	 * 
	 * @param inflow
	 * @return
	 * @throws IOException 
	 */
	public T deserializeFlow(ByteInflow inflow) throws IOException;
	
	/**
	 * 
	 * @param value
	 * @param outflow
	 * @throws IOException 
	 */
	public void serializeStock(K value, ByteOutflow outflow) throws IOException;
	
	
	/**
	 * 
	 * @param inflow
	 * @return
	 * @throws IOException 
	 */
	public K deserializeStock(ByteInflow inflow) throws IOException;
	
	
	/**
	 * 
	 * @param origin
	 * @return
	 */
	public K copyStock(K origin);
	
	
	/**
	 * 
	 * @param flow
	 * @return
	 */
	public void updateStock(K state, T flow);
	
}
