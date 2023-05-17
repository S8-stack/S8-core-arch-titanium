package com.s8.arch.magnesium.syncbase.store;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.core.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.core.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.UserMgOperation;
import com.s8.arch.magnesium.repobase.repository.MgRepositoryHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;


/**
 * 
 * @author pierreconvert
 *
 */
class CommitOp extends UserMgOperation<MgSyncStore> {

	@Override
	public boolean isModifyingResource() {
		return true;
	}
	
	
	public final MgSyncStoreHandler handler;
	
	public final String repositoryAddress;
	
	public final String branchName;
	
	public final NdObject[] objects;
	
	
	public final VersionMgCallback onSucceed;
	
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitOp(long timestamp,
			MgSyncStoreHandler handler, 
			String repositoryAddress,
			String branchName, 
			NdObject[] objects, 
			VersionMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchName = branchName;
		this.objects = objects;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}

	@Override
	public ConsumeResourceMgTask<MgSyncStore> createConsumeResourceTask(MgSyncStore repository) {
		return new ConsumeResourceMgTask<MgSyncStore>(repository) {

			@Override
			public MgHandler<MgSyncStore> getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "COMMIT-HEAD on "+branchName+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgSyncStore store) {
				try {
					store.repositoryHandlers.
					computeIfAbsent(repositoryAddress, address -> new MgRepositoryHandler(handler.ng, store, address)).
					commit(timeStamp, branchName, objects, onSucceed, onFailed);
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
