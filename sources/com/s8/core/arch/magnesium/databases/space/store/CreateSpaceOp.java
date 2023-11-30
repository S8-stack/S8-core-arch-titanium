package com.s8.core.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.space.requests.CreateSpaceS8Request;
import com.s8.api.flow.space.requests.CreateSpaceS8Request.Status;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.lithium.branches.LiBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class CreateSpaceOp extends RequestDbMgOperation<SpaceMgStore> {




	/**
	 * 
	 */
	public final SpaceMgDatabase spaceHandler;


	/**
	 * 
	 */
	public final CreateSpaceS8Request request;
	
	



	/**
	 * 
	 * @param handler
	 * @param onProcessed
	 * @param onFailed
	 */
	public CreateSpaceOp(long timestamp, S8User initiator, DbMgCallback callback,
			SpaceMgDatabase handler, CreateSpaceS8Request request) {
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


				MgSpaceHandler spaceHandler = store.createSpaceHandler(request.spaceId);

				if(spaceHandler != null) {

					LiBranch branch = new LiBranch(request.spaceId, store.getCodebase());
					branch.expose(request.exposure);
					
					spaceHandler.initializeResource(branch);
					
					request.onProcessed(Status.OK, 0x0L);
					
					/* before returning, notify next multi-db request can be launched */
					callback.call();
					
					return true;
				}
				else {

					/* exit point 2 -> soft fail */
					request.onProcessed(Status.SPACE_ID_CONFLICT, 0x0L);
					
					/* before returning, notify next multi-db request can be launched */
					callback.call();
					
					return false;
					
				}
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				
				/* before returning, notify next multi-db request can be launched */
				callback.call();
			}
		};
	}

}
