package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class CreateBranchOp extends RequestH3MgOperation<MgRepoStore> {


	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final String branchId;

	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;

	public final long options;



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
		this.storeHandler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.onSucceed = onSucceed;
		this.options = options;
	}


	@Override
	public H3MgHandler<MgRepoStore> getHandler() {
		return storeHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<MgRepoStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<MgRepoStore>(storeHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepoStore store) throws JOOS_CompilingException, IOException {

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
				return false;
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
