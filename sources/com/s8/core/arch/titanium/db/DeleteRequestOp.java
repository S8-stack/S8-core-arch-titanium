package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.DeleteTiRequest;
import com.s8.core.arch.titanium.db.requests.DeleteTiRequest.ResponseStatus;
import com.s8.core.arch.titanium.db.requests.TiRequest;


/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
class DeleteRequestOp<R> extends RequestOp<R> {

	public final DeleteTiRequest<R> request;

	public DeleteRequestOp(TiDbHandler<R> handler, DeleteTiRequest<R> request) {
		super(handler);
		this.request = request;
	}



	@Override
	public void perform() {
		thenDelete();
	}


	private void thenDelete() {
		handler.ng.pushAsyncTask(new AsyncSiTask() {

			@Override
			public MthProfile profile() { return MthProfile.IO_SSD; }


			@Override
			public void run() {

				/* retrieve resource */
				boolean isSucessful = handler.io_deleteResource();

				// continuation
				thenProceed(isSucessful);
			}

			@Override
			public String describe() {
				return "Load "+handler.key+" resources ...";
			}

		});
	}



	private void thenProceed(boolean isSucessful) {
		handler.ng.pushAsyncTask(new AsyncSiTask() {


			@Override
			public void run() {
				
				
				
				handler.io_deleteResource();
				
				handler.resource = null;
				handler.resourceStatus = TiResourceStatus.DELETED;
				handler.isSynced = true;

				
				request.onProcessed(ResponseStatus.SUCCESSFULLY_DELETED);
				
				terminate();
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



	@Override
	public TiRequest<R> getRequest() {
		return request;
	}

}
