package com.s8.arch.magnesium.handlers.h3;

import java.io.IOException;

public interface H3MgIOModule<R> {

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public R load() throws IOException;
	
	
	/**
	 * 
	 * @param resource
	 * @throws Exception
	 */
	public void save(R resource) throws IOException;
	
}
