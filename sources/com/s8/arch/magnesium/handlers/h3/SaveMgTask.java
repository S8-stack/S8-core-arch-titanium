package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public class SaveMgTask<R> implements AsyncTask {


	public final H3MgHandler<R> handler;
	
	public final R resource;
	
	/**
	 * 
	 * @param resource
	 * @param callback
	 */
	public SaveMgTask(H3MgHandler<R> handler, R resource) {
		super();
		this.handler = handler;
		this.resource = resource;
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
