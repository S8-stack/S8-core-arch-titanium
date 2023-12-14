package com.s8.core.arch.magnesium.databases.space.entry;

import com.s8.api.flow.S8User;
import com.s8.api.flow.space.objects.SpaceS8Object;
import com.s8.api.flow.space.requests.AccessSpaceS8Request;
import com.s8.api.flow.space.requests.AccessSpaceS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.lithium.branches.LiBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessSpaceOp extends RequestDbMgOperation<LiBranch> {





	public final MgSpaceHandler spaceHandler;



	/**
	 * 
	 */
	public final AccessSpaceS8Request request;
	
	



	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public AccessSpaceOp(long timestamp, S8User initiator, SiliconChainCallback callback,
			MgSpaceHandler spaceHandler, AccessSpaceS8Request request) {
		super(timestamp, initiator, callback);
		this.spaceHandler = spaceHandler;
		this.request = request;
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
				return "CLONE-HEAD on "+spaceHandler.getIdentifier()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(LiBranch branch) {
				SpaceS8Object[] objects = branch.getCurrentExposure();
				
				request.onAccessed(Status.OK, objects);
				
				boolean hasBeenModified = branch.getGraph().hasUnpublishedChanges();

				if(hasBeenModified && request.writeChangesImmediatelyAfter) {
					handler.save();
				}
				
				callback.call();

				return hasBeenModified;
			}


			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}


	@Override
	public H3MgHandler<LiBranch> getHandler() {
		return spaceHandler;
	}

}
