package com.s8.core.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.space.requests.AccessSpaceS8Request;
import com.s8.api.flow.space.requests.AccessSpaceS8Request.Status;
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
class AccessSpaceOp extends RequestDbMgOperation<SpaceMgStore> {




	/**
	 * space-handler
	 */
	public final SpaceMgDatabase spaceHandler;

	

	/**
	 * space-id
	 */
	public final AccessSpaceS8Request request;
	

	/**
	 * 
	 * @param handler
	 * @param onProcessed
	 * @param onFailed
	 */
	public AccessSpaceOp(long timestamp, S8User initiator, SiliconChainCallback callback,
			SpaceMgDatabase handler, AccessSpaceS8Request request) {
		super(timestamp, initiator, callback);
		this.spaceHandler = handler;
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
				return MthProfile.IO_SSD; 
			}

			@Override
			public String describe() {
				return "ACCESS-EXPOSURE on "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(SpaceMgStore store) throws IOException {


				MgSpaceHandler spaceHandler = store.getSpaceHandler(request.spaceId);

				if(spaceHandler != null) {
					/* exit point 1 -> continue */
					spaceHandler.accessSpace(timeStamp, initiator, callback, request);
				}
				else {
					/* exit point 2 -> soft fail */
					request.onAccessed(Status.SPACE_DOES_NOT_EXIST, null);
					callback.call();
				}
				
				/* no new space created */
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}

}
