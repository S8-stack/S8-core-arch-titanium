package com.s8.core.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.space.requests.ExposeSpaceS8Request;
import com.s8.api.flow.space.requests.ExposeSpaceS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends RequestDbMgOperation<SpaceMgStore> {

	/**
	 * 
	 */
	public final SpaceMgDatabase spaceHandler;


	public final ExposeSpaceS8Request request;
	


	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, S8User initiator, SiliconChainCallback callback,
			SpaceMgDatabase spaceHandler, ExposeSpaceS8Request request) {
		super(timestamp, initiator, callback);
		this.spaceHandler = spaceHandler;
		this.request = request;
	}

	@Override
	public SpaceMgDatabase getHandler() {
		return spaceHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<SpaceMgStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<SpaceMgStore>(spaceHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "ACCESS-EXPOSURE on "+spaceHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(SpaceMgStore store) throws IOException {

				MgSpaceHandler spaceHandler = store.getSpaceHandler(request.spaceId);
				if(spaceHandler != null) {
					spaceHandler.exposeObjects(timeStamp, initiator, callback, request);

					/* not change in the db itself, despite space will be modified */
					return false;
				}
				else {
					request.onResponse(Status.NOT_FOUND, 0x0L);
					callback.call();
					return false;
				}
			}


			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}

}
