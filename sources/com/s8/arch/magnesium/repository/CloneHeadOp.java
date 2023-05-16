package com.s8.arch.magnesium.repository;

import java.io.IOException;

import com.s8.arch.magnesium.branch.MgBranchHandler;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.handler.CatchExceptionMgTask;
import com.s8.arch.magnesium.handler.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handler.MgHandler;
import com.s8.arch.magnesium.handler.UserMgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends UserMgOperation<MgRepository> {


	@Override
	public boolean isModifyingResource() {
		return false;
	}
	
	
	
	public final MgRepositoryHandler handler;
	
	public final String branchName;
	
	public final ObjectsMgCallback onSucceed;
	
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgRepositoryHandler handler, String branchName, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.branchName = branchName;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<MgRepository> createConsumeResourceTask(MgRepository repo) {
		return new ConsumeResourceMgTask<MgRepository>(repo) {

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
				return "CLONE-HEAD on "+branchName+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgRepository repository) {
				try {
					MgBranchHandler branchHandler = repository.branchHandlers.get(branchName);
					if(branchHandler == null) {
						throw new IOException("No branch "+branchName+" for repo "+repository.address);
					}
					branchHandler.cloneHead(timeStamp, onSucceed, onFailed);
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
