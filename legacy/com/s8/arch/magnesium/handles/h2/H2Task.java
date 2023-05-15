package com.s8.arch.magnesium.handles.h2;

import com.s8.arch.silicon.async.AsyncTask;


/**
 * 
 * @author pierreconvert
 *
 */
public abstract class H2Task<M> implements AsyncTask {

	
	public final H2Handle<M> handle;
	

	public H2Task(H2Handle<M> handle) {
		super();
		this.handle = handle;
	}
	
	
}
