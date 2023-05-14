package com.s8.arch.magnesium.branch;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handler.MgSharedResourceHandler;
import com.s8.arch.magnesium.handler.UserMgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends UserMgOperation<NdBranch> {


	@Override
	public boolean isReadOnly() {
		return true;
	}
	
	
	
	public final MgBranchHandler handler;
	
	public final ObjectsMgCallback onSucceed;
	
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgBranchHandler handler, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<NdBranch> createConsumeResourceTask(NdBranch branch) {
		return new ConsumeResourceMgTask<NdBranch>(branch) {

			@Override
			public MgSharedResourceHandler<NdBranch> getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+handler.id+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(NdBranch branch) {
				try {
					NdObject[] objects = branch.cloneHead().exposure;
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
