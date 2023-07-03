package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
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
class CloneBranchVersionOp extends RequestH3MgOperation<MgRepoStore> {


	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final String branchId;

	public final long version;

	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;

	public final long options;


	/**
	 * 
	 * @param storeHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchVersionOp(long timestamp, 
			RepoMgDatabase storeHandler, 
			String repositoryAddress,
			String branchName, 
			long version, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.storeHandler = storeHandler;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchName;
		this.version = version;
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
				return "CLONE-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepoStore store) throws JOOS_CompilingException, IOException {
				MgRepositoryHandler repoHandler = store.getRepositoryHandler(repositoryAddress, false);

				if(repoHandler != null) {
					repoHandler.cloneVersion(timeStamp, branchId, version, onSucceed, options);
				}
				else {
					BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
					output.isSuccessful = false;
					output.isRepositoryDoesNotExist = true;
					onSucceed.call(output);
				}
				return false;
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
