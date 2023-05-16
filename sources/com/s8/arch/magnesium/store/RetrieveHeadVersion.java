package com.s8.arch.magnesium.store;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handler.MgHandler;
import com.s8.arch.magnesium.handler.UserMgOperation;
import com.s8.arch.magnesium.repository.MgRepositoryHandler;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class RetrieveHeadVersion extends UserMgOperation<MgStore> {


	@Override
	public boolean isModifyingResource() {
		return false;
	}



	public final MgStoreHandler handler;

	public final String repositoryAddress;
	
	public final String branchName;

	public final VersionMgCallback onSucceed;

	public final ExceptionMgCallback onFailed;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public RetrieveHeadVersion(long timestamp,
			MgStoreHandler handler, 
			String repositoryAddress,
			String branchName,
			VersionMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchName = branchName;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}

	@Override
	public ConsumeResourceMgTask<MgStore> createConsumeResourceTask(MgStore store) {
		return new ConsumeResourceMgTask<MgStore>(store) {

			@Override
			public MgHandler<MgStore> getHandler() {
				return handler;
			}

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchName+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgStore store) {
				try {
					store.repositoryHandlers.
					computeIfAbsent(repositoryAddress, address -> new MgRepositoryHandler(handler.ng, store, address)).
					retrieveHeadVersion(timeStamp, branchName, onSucceed, onFailed);
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
