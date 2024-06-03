package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.AccessTiRequest;
import com.s8.core.arch.titanium.db.requests.AccessTiRequest.ResponseStatus;
import com.s8.core.arch.titanium.db.requests.TiRequest;


/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
class AccessRequestOp<R> extends RequestOp<R> {

	public final AccessTiRequest<R> request;

	public AccessRequestOp(TiDbHandler<R> handler, AccessTiRequest<R> request) {
		super(handler);
		this.request = request;
	}



	@Override
	public void perform() {
		if(handler.resourceStatus != TiResourceStatus.UNDEFINED) {
			thenProceed();
		}
		else {
			/* if undefined, try to load first */ 
			thenLoad();
		}
	}




	private void thenLoad() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {

			@Override
			public MthProfile profile() { return MthProfile.IO_SSD; }


			@Override
			public void run() {

				handler.u_loadResource();

				// continuation
				thenProceed();
			}

			@Override
			public String describe() {
				return "Load "+handler.key+" resources ...";
			}
		});
	}


	private AccessTiRequest.ResponseStatus translate(){
		switch(handler.resourceStatus) {
		case OK : return ResponseStatus.SUCCESSFULLY_ACCESSED;
		case UNDEFINED : return ResponseStatus.NO_RESOURCE_FOR_KEY;
		case DELETED : return ResponseStatus.NO_RESOURCE_FOR_KEY;
		case FAILED_TO_LOAD : 
		default:
			return ResponseStatus.FAILED_TO_LOAD;
		}
	}


	private void thenProceed() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {

			@Override
			public void run() {

				boolean hasResourceBeenModified = request.onProcessed(handler.path, translate(), handler.resource);

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

				handler.u_saveResource();

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
