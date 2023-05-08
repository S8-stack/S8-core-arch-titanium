package com.s8.arch.magnesium.stores.m3.requests;

import java.io.IOException;

public interface M3Request<T> {

	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void serve() throws IOException;
}
