package com.s8.arch.magnesium.handlers.h2;

import com.s8.core.arch.silicon.async.AsyncSiTask;


/**
 * 
 * @author pierreconvert
 *
 */
public abstract class H2Task<M> implements AsyncSiTask {

	
	public final H2Handle<M> handle;
	

	public H2Task(H2Handle<M> handle) {
		super();
		this.handle = handle;
	}
	
	
}
