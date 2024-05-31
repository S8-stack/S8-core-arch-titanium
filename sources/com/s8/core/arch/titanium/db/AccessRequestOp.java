package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.AccessMgRequest;
import com.s8.core.arch.titanium.db.requests.MgRequest;


/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
class AccessRequestOp<R> extends RequestOp<R> {

	public final AccessMgRequest<R> request;

	public AccessRequestOp(MgDbHandler<R> handler, AccessMgRequest<R> request) {
		super(handler);
		this.request = request;
	}



	@Override
	public void perform() {
		if(handler.resourceStatus != null) {
			thenProceed();
		}
		else {
			thenLoad();
		}
	}




	private void thenLoad() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {

			@Override
			public MthProfile profile() { return MthProfile.IO_SSD; }


			@Override
			public void run() {
				try {

					/* retrieve resource */
					boolean hasResource = handler.io_loadResource();

					if(hasResource) {
						
						/* low-contention probability synchronized section */
						handler.resourceStatus = MgResourceStatus.OK;
					}
					else {
						handler.resourceStatus = MgResourceStatus.NO_RESOURCE_IN_DB;
						
					}
				} 
				catch (MgIOException exception) {
					handler.resource = null;
					handler.resourceStatus = exception.status;
				}

				/*
				 * Freshly loaded, do detachable
				 */
				handler.isSynced = true;

				// continuation
				thenProceed();
			}

			@Override
			public String describe() {
				return "Load "+handler.key+" resources ...";
			}
		});
	}



	private void thenProceed() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {

			@Override
			public void run() {

				boolean hasResourceBeenModified = request.onResourceAccessed(handler.path, handler.resourceStatus, handler.resource);

				/* check consequences of resource mod */
				if(hasResourceBeenModified) {
					handler.isSynced = false;			
				}


				if(request.isImmediateSyncRequired && !handler.isSynced) {
					thenSave();
				}
				else {
					terminate();
				}
			}

			@Override
			public String describe() {
				return request.describe();
			}

			@Override
			public MthProfile profile() {
				return request.profile();
			}
		});
	}


	private void thenSave() {

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

				terminate();
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
	public MgRequest<R> getRequest() {
		return request;
	}


}
