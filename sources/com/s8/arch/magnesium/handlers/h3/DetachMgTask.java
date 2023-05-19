package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.magnesium.callbacks.VoidMgCallback;

public abstract class DetachMgTask<R> extends ConsumeResourceMgTask<R> {
	
	public final VoidMgCallback callback;
	
	@Override
	public abstract H3MgHandler<R> getHandler();
	

	public DetachMgTask(R resource, VoidMgCallback callback) {
		super(resource);
		this.callback = callback;
	}

	@Override
	public void consumeResource(R resource) {

		try {
			/* save resource */
			save(resource);

			getHandler().notifySuccessfullySaved();
			
			// run call back
			callback.call();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
			getHandler().setFailed(e);
		}
	}

	public abstract void save(R resource) throws Exception;

	
}
