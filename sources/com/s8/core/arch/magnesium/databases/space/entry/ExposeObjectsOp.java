package com.s8.core.arch.magnesium.databases.space.entry;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.flow.S8User;
import com.s8.api.flow.space.requests.ExposeSpaceS8Request;
import com.s8.api.flow.space.requests.ExposeSpaceS8Request.Status;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.lithium.branches.LiBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends RequestDbMgOperation<LiBranch> {




	/**
	 * 
	 */
	public final MgSpaceHandler spaceHandler;


	public final ExposeSpaceS8Request request;
	
	




	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, S8User initiator, DbMgCallback callback,
			MgSpaceHandler spaceHandler, ExposeSpaceS8Request request) {
		super(timestamp, initiator, callback);
		this.spaceHandler = spaceHandler;
		this.request = request;
	}
	

	@Override
	public MgSpaceHandler getHandler() {
		return spaceHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<LiBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<LiBranch>(spaceHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+spaceHandler.getIdentifier()+" branch of "+spaceHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(LiBranch branch) throws S8IOException {

				/* ranges */
				if(request.exposure != null) {
					int range = request.exposure.length;
					for(int slot = 0; slot < range; slot++) {
						branch.expose(slot, request.exposure[slot]);	
					}	
				}

				request.onResponse(Status.OK, 0x0L);// TODO version
				
				if(request.saveImmediatelyAfter) {
					handler.save();
				}
				
				callback.call();
				
				return true;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}


}
