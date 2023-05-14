package com.s8.arch.magnesium.shared;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public class SaveMgTask<R> implements AsyncTask {


	public final MgSharedResourceHandler<R> handler;
	
	public final R resource;
	
	/**
	 * 
	 * @param resource
	 * @param callback
	 */
	public SaveMgTask(MgSharedResourceHandler<R> handler, R resource) {
		super();
		this.handler = handler;
		this.resource = resource;
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
		return "Saving resource for handler: "+handler.getName();
	}



	@Override
	public MthProfile profile() {
		return MthProfile.IO_SSD;
	}
	


}
