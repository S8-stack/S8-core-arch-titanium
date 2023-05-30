package com.s8.arch.magnesium.databases.repo.store;

import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repo.repository.MgRepositoryHandler;
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
class CreateBranchOp extends UserH3MgOperation<MgRepoStore> {


	public final RepoMgDatabase handler;

	public final String repositoryAddress;

	public final String branchId;

	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;

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
	public CreateBranchOp(long timestamp, 
			RepoMgDatabase handler, 
			String repositoryAddress,
			String branchId,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);

		/* fields */
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.onSucceed = onSucceed;
		this.options = options;
	}


	@Override
	public ConsumeResourceMgTask<MgRepoStore> createConsumeResourceTask(MgRepoStore store) {
		return new ConsumeResourceMgTask<MgRepoStore>(store) {

			@Override
			public H3MgHandler<MgRepoStore> getHandler() {
				return handler;
			}

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgRepoStore store) {
				try {

					MgRepositoryHandler repoHandler = store.getRepositoryHandler(repositoryAddress, false);
					if(repoHandler != null) {
						repoHandler.createBranch(timeStamp, branchId, onSucceed, options);
					}
					else {
						BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
						output.isSuccessful = false;
						output.isRepositoryDoesNotExist = true;
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
