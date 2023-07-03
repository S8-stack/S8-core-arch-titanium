package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
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
class RetrieveBranchHeadVersion extends RequestH3MgOperation<MgRepoStore> {





	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final String branchName;

	public final MgCallback<BranchVersionS8AsyncOutput> onDone;

	public final long options;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public RetrieveBranchHeadVersion(long timestamp,
			RepoMgDatabase handler, 
			String repositoryAddress,
			String branchName,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.storeHandler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchName = branchName;
		this.onDone = onSucceed;
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
				return "CLONE-HEAD on "+branchName+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepoStore store) throws JOOS_CompilingException, IOException {
				MgRepositoryHandler repoHandler = store.getRepositoryHandler(repositoryAddress, false);
				if(repoHandler != null) {
					repoHandler.retrieveHeadVersion(timeStamp, branchName, onDone, options);
				}
				else {
					BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
					output.isSuccessful = false;
					output.isRepositoryDoesNotExist = true;
					onDone.call(output);
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
				output.reportException(exception);
				onDone.call(output);
			}			
		};
	}

}
