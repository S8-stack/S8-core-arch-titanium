package com.s8.arch.magnesium.repobase.repository;

import java.io.IOException;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.core.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.core.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.UserMgOperation;
import com.s8.arch.magnesium.repobase.branch.MgBranchHandler;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneVersionOp extends UserMgOperation<MgRepository> {
	
	
	public final MgRepositoryHandler handler;
	
	public final String branchId;
	
	public final long version;
	
	public final ObjectsMgCallback onSucceed;

	public final ExceptionMgCallback onFailed;

	
	@Override
	public boolean isModifyingResource() {
		return false;
	}
	
	
	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneVersionOp(long timestamp, 
			MgRepositoryHandler handler, String branchId, long version, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.branchId = branchId;
		this.version = version;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<MgRepository> createConsumeResourceTask(MgRepository repository) {
		return new ConsumeResourceMgTask<MgRepository>(repository) {

			@Override
			public MgHandler<MgRepository> getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgRepository repository) {
				try {
					MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
					
					if(branchHandler == null) { throw new IOException("No branch "+branchId+" on repo "+repository.address); }
					branchHandler.cloneVersion(timeStamp, version, onSucceed, onFailed);
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
