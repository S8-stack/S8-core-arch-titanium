package com.s8.arch.magnesium.repobase.repository;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.silicon.async.AsyncTask;

/**
 * 
 * @author pierreconvert
 *
 */
public abstract class MgRepositoryOperation {

	
	/**
	 * 
	 */
	public final MgRepositoryHandler handler;
	
	
	/**
	 * 
	 */
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onFailed
	 */
	public MgRepositoryOperation(MgRepositoryHandler handler, ExceptionMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onFailed = onFailed;
	}


	/**
	 * 
	 * @return
	 */
	public abstract AsyncTask createTask();
	
}
