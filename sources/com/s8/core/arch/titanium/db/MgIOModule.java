package com.s8.core.arch.titanium.db;

import java.io.IOException;
import java.nio.file.Path;

public interface MgIOModule<R> {

	
	
	/**
	 * Fast check of resource existence
	 * @return
	 */
	public boolean hasResource(Path path);
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public R readResource(Path path) throws MgIOException;
	
	
	/**
	 * 
	 * @param resource
	 * @throws Exception
	 */
	public void writeResource(Path path, R resource) throws IOException;
	
	
	/**
	 * Fast deletion of resource existence (save disk space)
	 * @return
	 */
	public boolean deleteResource(Path path);
	
}
