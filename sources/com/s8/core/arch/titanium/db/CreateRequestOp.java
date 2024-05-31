package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.CreateMgRequest;
import com.s8.core.arch.titanium.db.requests.MgRequest;


/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
class CreateRequestOp<R> extends RequestOp<R> {

	public final CreateMgRequest<R> request;

	public CreateRequestOp(MgDbHandler<R> handler, CreateMgRequest<R> request) {
		super(handler);
		this.request = request;
	}



	@Override
	public void perform() {

		/**
		 * Override is not enabled and resource is available
		 */
		if(!request.isOverridingEnabled && (handler.isResourceAvailable())) {
			thenProceed(false);
		}
		else if(request.isOverridingEnabled ||
				/* resource has been tested for loading and failed */
				(handler.resourceStatus != null && !handler.resourceStatus.isAvailable())) {
			thenProceed(true);
		}

		else {
			/* check first */
			thenCheck();
		}
	}




	private void thenCheck() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {

			@Override
			public MthProfile profile() { return MthProfile.IO_SSD; }


			@Override
			public void run() {

				/* retrieve resource */
				boolean hasResource = handler.io_hasResource();

				boolean isAllowed = request.isOverridingEnabled || 
						(!request.isOverridingEnabled && !hasResource);
				
				if(isAllowed) {
					handler.io_deleteResource();
				}

				// continuation
				thenProceed(isAllowed);
			}

			@Override
			public String describe() {
				return "Load "+handler.key+" resources ...";
			}

		});
	}



	private void thenProceed(boolean isAllowed) {
		handler.ng.pushAsyncTask(new AsyncSiTask() {


			@Override
			public void run() {

				request.onPathGenerated(handler.path);
				
				if(isAllowed) {
					handler.resource = request.resource;
					handler.resourceStatus = MgResourceStatus.OK;
					handler.isSynced = false;			
				}

				request.onEntryCreated(true);
				
				if(request.isResourceSavedToDisk) {
					thenSave();
				}
				else {
					terminate();					
				}
			}

			@Override
			public String describe() {
				return "writing the resource for creation";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
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
