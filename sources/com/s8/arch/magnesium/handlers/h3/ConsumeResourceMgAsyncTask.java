package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.magnesium.handlers.h3.H3MgHandler.Status;
import com.s8.arch.silicon.async.SiAsyncTask;

public abstract class ConsumeResourceMgAsyncTask<R> implements SiAsyncTask {


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
		Exception handlerException = null;
		boolean isResourceAvailable = false;

		synchronized (handler.lock) {
			isResourceAvailable = (handler.status == Status.LOADED);
			if(isResourceAvailable) {
				resource = handler.resource;
			}
			else {
				handlerException = handler.exception;
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
			catch(Exception exception) {
				catchException(exception);
			}
		}
		else {
			catchException(handlerException);
		}



		/**
		 * handler
		 */
		handler.roll(true);
	}

}
