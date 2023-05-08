package com.s8.arch.magnesium.stores.m3;

import java.io.IOException;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public interface M3ModelPrototype<T> {

	
	/**
	 * 
	 * @return
	 */
	public Class<?> getBaseType();
	
	
	/**
	 * 
	 * @param value
	 * @param outflow
	 * @throws IOException 
	 */
	public void serialize(T value, ByteOutflow outflow) throws IOException;
	
	
	/**
	 * 
	 * @param inflow
	 * @return
	 * @throws IOException 
	 */
	public T deserialize(ByteInflow inflow) throws IOException;
	
	
}
