package com.s8.core.arch.magnesium.handlers.h3;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public class SaveMgAsyncTask<R> implements AsyncSiTask {


	public final H3MgHandler<R> handler;
	
	
	/**
	 * 
	 * @param resource
	 * @param callback
	 */
	public SaveMgAsyncTask(H3MgHandler<R> handler) {
		super();
		this.handler = handler;
	}

	

	@Override
	public void run() {
		
		/**
		 * run callback on resource
		 */

		// save sub handlers
		handler.getSubHandlers().forEach(subHandler -> subHandler.save());
		
		try {
			/* save resource */
			if(!handler.isSaved()) {
				
				handler.getIOModule().save(handler.getResource());
				
				handler.notifySuccessfullySaved();	
			}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
			handler.setFailed(e);
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
