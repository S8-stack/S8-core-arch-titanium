package com.s8.core.arch.magnesium.db;

import java.nio.file.Path;


/**
 * 
 */
public interface MgPathComposer {

	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Path composePath(String key);
	
}
