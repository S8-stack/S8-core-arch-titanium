package com.s8.arch.magnesium.databases.repo.repository;

import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repo.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;


/**
 * 
 * @author pierreconvert
 *
 */
class CreateBranchOp extends UserH3MgOperation<MgRepository> {

	@Override
	public boolean isModifyingResource() {
		return true;
	}
	
	
	public final MgRepositoryHandler handler;
	
	public final String branchId;
	
	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;
	
	public final long options;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CreateBranchOp(long timestamp,
			MgRepositoryHandler handler, String branchId,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.handler = handler;
		this.branchId = branchId;
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
				return "COMMIT-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgRepository repository) {
				try {
					MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
					if(branchHandler == null) {
						
						branchHandler = new MgBranchHandler(handler.ng, handler.store, repository);
						
						NdBranch branch = new NdBranch(handler.store.codebase, branchId);
						branchHandler.initializeResource(branch);
						
						repository.branchHandlers.put(branchId, branchHandler);
						
						onSucceed.call(BranchCreationS8AsyncOutput.successful(0x0L));
					}
					else {
						BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
						output.isSuccessful = false;
						output.hasIdConflict = true;
						onSucceed.call(output);
					}
				}
				catch(Exception exception) {
					BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
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
				BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}

	
}
