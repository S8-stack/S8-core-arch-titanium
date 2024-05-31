package com.s8.core.arch.titanium.handlers.h3;

import com.s8.core.arch.silicon.async.AsyncSiTask;

public abstract class ConsumeResourceMgAsyncTask<R> implements AsyncSiTask {


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
			
			/* load resource status */
			isResourceAvailable = handler.state.isResourceAvailable;
			
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
					handler.notifyModifiedResource();
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
