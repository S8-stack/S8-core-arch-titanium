package com.s8.arch.magnesium.handlers.h2;

import java.io.IOException;


/**
 * 
 * @author pierreconvert
 *
 */

public interface H2ModelPrototype<M> {
	
	

	/**
	 * 
	 * @param address
	 * @return
	 */
	public abstract M load() throws IOException;

	
	/**
	 * 
	 * @param model
	 * @param address
	 */
	public abstract void save(M model) throws IOException;
	

	/**
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	public long getBytecount(M model) throws IOException;

}
