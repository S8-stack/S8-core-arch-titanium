package com.s8.stack.arch.magnesium.stores.m1;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.silicon.SiException;


/**
 * 
 * @author pierreconvert
 *
 * @param <M>
 */
public interface M1ModelConstructor<M> {

	
	
	/**
	 * 
	 * @param path
	 * @param address
	 * @return
	 * @throws IOException 
	 * @throws SiException 
	 */
	public M init(Path path, String address) throws IOException, SiException;
	
	
}
