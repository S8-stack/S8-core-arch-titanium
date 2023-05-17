package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncTask;

public abstract class ConsumeResourceMgTask<R> implements AsyncTask {


	public final R resource;
	
	public ConsumeResourceMgTask(R resource) {
		super();
		this.resource = resource;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public abstract H3MgHandler<R> getHandler();
	
	
	/**
	 * 
	 * @param resource
	 */
	public abstract void consumeResource(R resource);
	

	@Override
	public void run() {
		
		/**
		 * run callback on resource
		 */
		consumeResource(resource);
		
		/**
		 * 
		 */
		getHandler().roll(true);
	}
	
}
