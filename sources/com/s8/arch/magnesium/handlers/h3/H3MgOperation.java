package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncTask;

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
	 */
	public abstract boolean isModifyingResource();
	
	/**
	 * 
	 * @param resource
	 */
	public abstract AsyncTask createConsumeResourceTask(R resource);

	
	/**
	 * 
	 * @param exception
	 */
	public abstract AsyncTask createCatchExceptionTask(Exception exception);


}
