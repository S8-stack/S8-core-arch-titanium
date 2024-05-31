package com.s8.core.arch.titanium.handlers.h3;

import com.s8.core.arch.titanium.callbacks.VoidMgCallback;

public abstract class DetachMgAsyncTask<R> extends ConsumeResourceMgAsyncTask<R> {
	
	public final VoidMgCallback callback;
	
	

	public DetachMgAsyncTask(H3MgHandler<R> handler, VoidMgCallback callback) {
		super(handler);
		this.callback = callback;
	}
	

	@Override
	public boolean consumeResource(R resource) {

		try {
			/* save resource */
			save(resource);

			/* */
			handler.notifySuccessfullySaved();
			
			// run call back
			callback.call();

			/* resource has not been modified */
			return false;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
			handler.setFailed(e);
			
			/* resource has not been modified */
			return false;
		}
	}

	public abstract void save(R resource) throws Exception;

	
}
