package com.s8.arch.magnesium.handlers.h2;

import com.s8.arch.silicon.async.SiAsyncTask;


/**
 * 
 * @author pierreconvert
 *
 */
public abstract class H2Task<M> implements SiAsyncTask {

	
	public final H2Handle<M> handle;
	

	public H2Task(H2Handle<M> handle) {
		super();
		this.handle = handle;
	}
	
	
}
