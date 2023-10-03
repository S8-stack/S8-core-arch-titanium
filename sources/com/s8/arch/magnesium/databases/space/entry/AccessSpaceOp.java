package com.s8.arch.magnesium.databases.space.entry;

import com.s8.api.bytes.Bool64;
import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.lithium.branches.LiBranch;

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
	public final MgCallback<SpaceExposureS8AsyncOutput> onSucceed;



	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public AccessSpaceOp(long timestamp, S8User initiator, MgSpaceHandler spaceHandler, 
			MgCallback<SpaceExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.spaceHandler = spaceHandler;
		this.onSucceed = onSucceed;
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
				SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();

				SpaceS8Object[] objects = branch.getCurrentExposure();
				output.isSuccessful = true;
				output.objects = objects;

				onSucceed.call(output);

				boolean hasBeenModified = branch.getGraph().hasUnpublishedChanges();

				if(hasBeenModified && Bool64.has(options, S8AsyncFlow.SAVE_IMMEDIATELY_AFTER)) {
					handler.save();
				}

				return hasBeenModified;
			}


			@Override
			public void catchException(Exception exception) {
				SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
				output.isSuccessful = false;
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}


	@Override
	public H3MgHandler<LiBranch> getHandler() {
		return spaceHandler;
	}

}
