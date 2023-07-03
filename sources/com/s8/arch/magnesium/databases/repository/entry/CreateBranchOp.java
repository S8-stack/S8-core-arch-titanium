package com.s8.arch.magnesium.databases.repository.entry;

import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.arch.magnesium.databases.repository.store.MgRepoStore;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;


/**
 * 
 * @author pierreconvert
 *
 */
class CreateBranchOp extends RequestH3MgOperation<MgRepository> {

	
	public final MgRepositoryHandler repoHandler;
	
	public final String branchId;
	
	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;
	
	public final long options;

	public final MgRepoStore store;
	
	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CreateBranchOp(long timestamp,
			MgRepositoryHandler repoHandler, String branchId,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.repoHandler = repoHandler;
		this.branchId = branchId;
		this.onSucceed = onSucceed;
		this.options = options;
		
		
		this.store = repoHandler.store;
	}
	
	
	@Override
	public H3MgHandler<MgRepository> getHandler() {
		return repoHandler;
	}
	
	
	@Override
	public ConsumeResourceMgAsyncTask<MgRepository> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<MgRepository>(repoHandler) {
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "COMMIT-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {
				try {
					MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
					if(branchHandler == null) {
						
						branchHandler = new MgBranchHandler(handler.ng, store, repository);
						
						NdBranch branch = new NdBranch(store.codebase, branchId);
						branchHandler.initializeResource(branch);
						
						repository.branchHandlers.put(branchId, branchHandler);
						
						onSucceed.call(BranchCreationS8AsyncOutput.successful(0x0L));
						return true;
					}
					else {
						BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
						output.isSuccessful = false;
						output.hasIdConflict = true;
						onSucceed.call(output);
						return false;
					}
				}
				catch(Exception exception) {
					BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
					output.reportException(exception);
					onSucceed.call(output);
					return false;
				}
				
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
