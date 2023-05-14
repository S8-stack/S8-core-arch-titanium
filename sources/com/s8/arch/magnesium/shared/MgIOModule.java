package com.s8.arch.magnesium.shared;

public interface MgIOModule<R> {

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public R load() throws Exception;
	
	
	/**
	 * 
	 * @param resource
	 * @throws Exception
	 */
	public void save(R resource) throws Exception;
	
}
