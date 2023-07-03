package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.magnesium.handlers.h3.H3MgHandler.Status;
import com.s8.arch.silicon.async.AsyncTask;

public abstract class ConsumeResourceMgAsyncTask<R> implements AsyncTask {


	/**
	 * resource
	 */


	public final H3MgHandler<R> handler;
	
	


	/**
	 * 
	 * @param resource
	 */
	public ConsumeResourceMgAsyncTask(H3MgHandler<R> handler) {
		super();
		this.handler = handler;
	}



	/**
	 * 
	 * @param resource
	 */
	public abstract boolean consumeResource(R resource) throws Exception;



	/**
	 * 
	 * @param resource
	 */
	public abstract void catchException(Exception exception);


	@Override
	public void run() {


		R resource = null;
		Exception exception = null;
		boolean isResourceAvailable = false;

		synchronized (handler.lock) {
			isResourceAvailable = (handler.status == Status.LOADED);
			if(isResourceAvailable) {
				resource = handler.resource;
			}
			else {
				exception = handler.exception;
			}
		}



		if(isResourceAvailable) {

			/**
			 * run callback on resource
			 */
			try {
				boolean hasResourceBeenModified = consumeResource(resource);
					
				/* check consequences of resource mod */
				if(hasResourceBeenModified) {
					synchronized (handler.lock) {
						handler.isSaved = false;
					}
				}
				
			}
			catch(Exception e) {
				catchException(exception);
			}
		}
		else {
			catchException(exception);
		}



		/**
		 * handler
		 */
		handler.roll(true);
	}

}
