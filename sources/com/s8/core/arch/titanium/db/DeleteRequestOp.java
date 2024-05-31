package com.s8.core.arch.titanium.db;

import com.s8.core.arch.silicon.async.AsyncSiTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.DeleteMgRequest;
import com.s8.core.arch.titanium.db.requests.MgRequest;


/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
class DeleteRequestOp<R> extends RequestOp<R> {

	public final DeleteMgRequest<R> request;

	public DeleteRequestOp(MgDbHandler<R> handler, DeleteMgRequest<R> request) {
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
				handler.resourceStatus = MgResourceStatus.DELETED;
				handler.isSynced = true;

				
				request.onDelete(isSucessful);
				
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
	public MgRequest<R> getRequest() {
		return request;
	}

}
