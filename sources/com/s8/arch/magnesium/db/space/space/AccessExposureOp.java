package com.s8.arch.magnesium.db.space.space;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.object.LiObject;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessExposureOp extends UserH3MgOperation<LiBranch> {


	
	public @Override boolean isModifyingResource() { return false; }
	
	
	/**
	 * 
	 */
	public final MgSpaceHandler handler;
	
	
	/**
	 * 
	 */
	public final ObjectsMgCallback onSucceed;
	
	
	/**
	 * 
	 */
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public AccessExposureOp(long timestamp, MgSpaceHandler handler, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
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
				try {
					LiObject[] objects = branch.getCurrentExposure();
					onSucceed.call(objects);
				}
				catch(Exception exception) { onFailed.call(exception); }
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
				onFailed.call(exception);
			}
		};
	}


	
}
