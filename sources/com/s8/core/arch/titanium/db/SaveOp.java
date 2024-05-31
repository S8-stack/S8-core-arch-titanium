package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
class SaveOp<R> extends MgOperation<R> {

	/**
	 * 
	 * @param handler
	 */
	public SaveOp(MgDbHandler<R> handler) {
		super(handler);
	}

	@Override
	public void perform() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {
			
			@Override
			public void run() {
				
				/**
				 * run callback on resource
				 */

			
				try {
					/* save resource */
					if(!handler.isSynced) {
						
						handler.io_saveResource();
						handler.resourceStatus = MgResourceStatus.OK;
						handler.isSynced = true;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					
					handler.resourceStatus = MgResourceStatus.FAILED_TO_SAVE;
					handler.isSynced = true;
				}
				
				/**
				 * continuation
				 */
				handler.roll(true);
			}

			
			@Override
			public String describe() {
				return "Saving resource for handler: "+handler.key;
			}


			@Override
			public MthProfile profile() {
				return MthProfile.IO_SSD;
			}
		});
	}

	@Override
	public long getTimestamp() {
		return -8L;
	}
	

}
