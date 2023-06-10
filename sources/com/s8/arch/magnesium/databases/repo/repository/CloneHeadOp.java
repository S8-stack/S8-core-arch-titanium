package com.s8.arch.magnesium.databases.repo.repository;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repo.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends RequestH3MgOperation<MgRepository> {


	@Override
	public boolean isModifyingResource() {
		return false;
	}
	
	
	
	public final MgRepositoryHandler handler;
	
	public final String branchId;
	
	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;
	
	public final long options;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgRepositoryHandler handler, String branchId, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.handler = handler;
		this.branchId = branchId;
		this.onSucceed = onSucceed;
		this.options = options;
	}
	

	@Override
	public ConsumeResourceMgTask<MgRepository> createConsumeResourceTask(MgRepository repo) {
		return new ConsumeResourceMgTask<MgRepository>(repo) {

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
					if(branchHandler != null) {
						branchHandler.cloneHead(timeStamp, onSucceed, options);
					}
					else {
						BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
						output.isSuccessful = false;
						output.isBranchDoesNotExist = true;
						onSucceed.call(output);
					}
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
