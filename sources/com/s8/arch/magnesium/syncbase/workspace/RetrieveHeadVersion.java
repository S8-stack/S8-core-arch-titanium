package com.s8.arch.magnesium.syncbase.workspace;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.core.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.core.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.UserMgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class RetrieveHeadVersion extends UserMgOperation<NdBranch> {


	@Override
	public boolean isModifyingResource() {
		return false;
	}
	
	

	public final MgWorkspaceHandler handler;

	public final VersionMgCallback onSucceed;

	public final ExceptionMgCallback onFailed;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public RetrieveHeadVersion(long timestamp,
			MgWorkspaceHandler handler, 
			VersionMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}

	@Override
	public ConsumeResourceMgTask<NdBranch> createConsumeResourceTask(NdBranch branch) {
		return new ConsumeResourceMgTask<NdBranch>(branch) {

			@Override
			public MgHandler<NdBranch> getHandler() {
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
			public void consumeResource(NdBranch branch) {
				try {
					long version = branch.getHeadVersion();
					onSucceed.call(version);
				}
				catch(Exception exception) {
					onFailed.call(exception);
				}
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
