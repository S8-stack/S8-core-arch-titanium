package com.s8.stack.arch.magnesium.stores.m1;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 
 * @author pierreconvert
 *
 */
public interface M1ModelPrototype<M> {

	
	/**
	 * 
	 * @param model
	 * @return
	 */
	public long getBytecount(M model);
	
	
	
	/**
	 * 
	 * @param address
	 * @return
	 * @throws IOException 
	 */
	public M load(Path path, String address) throws IOException;

	
	
	/**
	 * 
	 * @param model
	 * @param address
	 * @throws IOException 
	 */
	public void save(Path path, M model) throws IOException;

}
