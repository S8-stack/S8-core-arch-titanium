package com.s8.arch.magnesium.shared;

import com.s8.arch.silicon.async.AsyncTask;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public abstract class MgOperation<R> {

	

	
	public abstract boolean isUserInduced();
	
	
	/**
	 * 
	 * @return
	 */
	public abstract long getTimestamp();
	
	
	/**
	 * 
	 */
	public abstract boolean isReadOnly();
	
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
