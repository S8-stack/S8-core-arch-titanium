package com.s8.arch.magnesium.databases.space.space;

import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.object.LiObject;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessExposureOp extends RequestH3MgOperation<LiBranch> {


	
	public @Override boolean isModifyingResource() { return false; }
	
	
	/**
	 * 
	 */
	public final MgSpaceHandler handler;
	
	
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
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public AccessExposureOp(long timestamp, MgSpaceHandler handler, MgCallback<SpaceExposureS8AsyncOutput> onSucceed, long options) {
		super(timestamp);
		this.handler = handler;
		this.onSucceed = onSucceed;
		this.options = options;
	}
	

	@Override
	public ConsumeResourceMgTask<LiBranch> createConsumeResourceTask(LiBranch branch) {
		return new ConsumeResourceMgTask<LiBranch>(branch) {

			@Override
			public MgSpaceHandler getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+handler.getIdentifier()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(LiBranch branch) {
				SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
				try {
					LiObject[] objects = branch.getCurrentExposure();
					output.isSuccessful = true;
					output.objects = objects;
				}
				catch(Exception exception) { 
					output.isSuccessful = false;
					output.reportException(exception);
				}
				onSucceed.call(output);
			}
		};
	}

	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) { 
		return new CatchExceptionMgTask(exception) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "failed to access resource on "+handler.getName()+": catching exception";
			}

			@Override
			public void catchException(Exception exception) {
				SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}


	
}
