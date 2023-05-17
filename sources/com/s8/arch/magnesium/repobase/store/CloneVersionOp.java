package com.s8.arch.magnesium.repobase.store;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.core.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.core.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.UserMgOperation;
import com.s8.arch.magnesium.repobase.repository.MgRepositoryHandler;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneVersionOp extends UserMgOperation<MgRepoStore> {
	
	
	public final MgRepoStoreHandler handler;

	public final String repositoryAddress;
	
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
			MgRepoStoreHandler handler, 
			String repositoryAddress,
			String branchName, 
			long version, 
			ObjectsMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchName;
		this.version = version;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<MgRepoStore> createConsumeResourceTask(MgRepoStore store) {
		return new ConsumeResourceMgTask<MgRepoStore>(store) {

			@Override
			public MgHandler<MgRepoStore> getHandler() {
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
			public void consumeResource(MgRepoStore store) {
				try {
					store.repositoryHandlers.
					computeIfAbsent(repositoryAddress, address -> new MgRepositoryHandler(handler.ng, store, address)).
					cloneVersion(timeStamp, branchId, version, onSucceed, onFailed);
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
