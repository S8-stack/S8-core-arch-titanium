package com.s8.arch.magnesium.databases.repo.repository;

import java.io.IOException;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repo.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneVersionOp extends UserH3MgOperation<MgRepository> {
	
	
	public final MgRepositoryHandler handler;
	
	public final String branchId;
	
	public final long version;
	
	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;

	public final long options;

	
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
			MgRepositoryHandler handler, String branchId, long version, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.handler = handler;
		this.branchId = branchId;
		this.version = version;
		this.onSucceed = onSucceed;
		this.options = options;
	}
	

	@Override
	public ConsumeResourceMgTask<MgRepository> createConsumeResourceTask(MgRepository repository) {
		return new ConsumeResourceMgTask<MgRepository>(repository) {

			@Override
			public H3MgHandler<MgRepository> getHandler() {
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
					
					if(branchHandler == null) { 
						throw new IOException("No branch "+branchId+" on repo "+repository.address); 
					}
					branchHandler.cloneVersion(timeStamp, version, onSucceed, options);
				}
				catch(Exception exception) { 
					BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
					output.reportException(exception);
					onSucceed.call(output);
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
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}


	
}
