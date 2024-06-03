package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.CreateTiRequest;
import com.s8.core.arch.titanium.db.requests.CreateTiRequest.ReturnedStatus;
import com.s8.core.arch.titanium.db.requests.TiRequest;


/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
class CreateRequestOp<R> extends RequestOp<R> {

	public final CreateTiRequest<R> request;

	public CreateRequestOp(TiDbHandler<R> handler, CreateTiRequest<R> request) {
		super(handler);
		this.request = request;
	}



	@Override
	public void perform() {
		/* resource loading have already been tried */
		if(handler.resourceStatus != TiResourceStatus.UNDEFINED) {
			
			/* override is enable, OR resource is not longer usable: defacto allowing override */
			if(request.isOverridingEnabled || handler.resourceStatus != TiResourceStatus.OK) {
				thenProceed();
			}
			
			/* cannot proceed since cannot override */
			else {
				request.onProcessed(ReturnedStatus.CONFLICT_ON_KEY);
				terminate();
			}
		}
		else { /* undefined status */
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


				/* can proceed */
				if(request.isOverridingEnabled || 
						(!request.isOverridingEnabled && !hasResource)) {

					if(hasResource) {
						handler.io_deleteResource();
					}

					// continuation
					thenProceed();

				}
				else {
					request.onProcessed(ReturnedStatus.CONFLICT_ON_KEY);
					terminate();
				}
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
				
				/* can proceed */
				if(handler.io_hasResource()) {
					handler.io_deleteResource();
				}
				
				request.onPathGenerated(handler.path);


				handler.resource = request.resource;
				handler.resourceStatus = TiResourceStatus.OK;
				handler.isSynced = false;			

				request.onProcessed(ReturnedStatus.SUCCESSFULLY_CREATED);

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
						handler.resourceStatus = TiResourceStatus.OK;
						handler.isSynced = true;
					}
				}
				catch (Exception e) {
					e.printStackTrace();

					handler.resourceStatus = TiResourceStatus.FAILED_TO_SAVE;
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
	public TiRequest<R> getRequest() {
		return request;
	}

}
