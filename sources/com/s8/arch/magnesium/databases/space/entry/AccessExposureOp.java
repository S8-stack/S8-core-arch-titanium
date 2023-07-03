package com.s8.arch.magnesium.databases.space.entry;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bytes.alpha.Bool64;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessExposureOp extends RequestH3MgOperation<LiBranch> {





	public final MgSpaceHandler spaceHandler;



	/**
	 * 
	 */
	public final MgCallback<SpaceExposureS8AsyncOutput> onSucceed;


	/**
	 * 
	 */
	public final long options;


	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public AccessExposureOp(long timestamp, MgSpaceHandler spaceHandler, 
			MgCallback<SpaceExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.spaceHandler = spaceHandler;
		this.onSucceed = onSucceed;
		this.options = options;
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

				LiObject[] objects = branch.getCurrentExposure();
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
