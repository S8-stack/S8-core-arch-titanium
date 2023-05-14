package com.s8.arch.magnesium.shared;

import com.s8.arch.magnesium.callbacks.VoidMgCallback;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;

public class UnmountMgTask<R> implements AsyncTask {


	/**
	 * 
	 */
	public final MgSharedResourceHandler<R> handler;

	public final R resource;
	
	public final long cutOffTimestamp;

	public final VoidMgCallback callback;


	/**
	 * 
	 * @param resource
	 * @param callback
	 */
	public UnmountMgTask(MgSharedResourceHandler<R> handler, R resource, long cutOffTimestamp, VoidMgCallback callback) {
		super();
		this.handler = handler;
		this.resource = resource;
		this.cutOffTimestamp = cutOffTimestamp;
		this.callback = callback;
	}



	@Override
	public void run() {

		/**
		 * run callback on resource
		 */

		try {
			/* save resource */
			/* save resource */
			if(!handler.isSaved()) {

				handler.getIOModule().save(resource);

				handler.notifySuccessfullySaved();	
			}


			/* activate callback */
			if(handler.isDetachable(cutOffTimestamp)) {
				callback.call();
			}
		}
		catch (Exception e) {
			e.printStackTrace();

			handler.raiseException(e);
		}

		/**
		 * 
		 */
		handler.roll(true);
	}



	@Override
	public String describe() {
		return "Unmounting resource for handler: "+handler.getName();
	}



	@Override
	public MthProfile profile() {
		return MthProfile.IO_SSD;
	}

}
