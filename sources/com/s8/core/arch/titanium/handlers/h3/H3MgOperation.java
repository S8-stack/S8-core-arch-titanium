package com.s8.core.arch.titanium.handlers.h3;

import com.s8.core.arch.silicon.async.AsyncSiTask;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public abstract class H3MgOperation<R> {

	

	
	public abstract boolean isUserInduced();
	
	
	/**
	 * 
	 * @return
	 */
	public abstract long getTimestamp();
	
	
	
	/**
	 * 
	 * @return
	 */
	public abstract H3MgHandler<R> getHandler();
	
	
	
	/**
	 * 
	 * @param resource
	 */
	public abstract AsyncSiTask createAsyncTask();




}
