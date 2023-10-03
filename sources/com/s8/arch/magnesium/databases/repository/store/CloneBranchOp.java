package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneBranchOp extends RequestDbMgOperation<RepoMgStore> {


	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final String branchId;

	public final long version;

	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;


	/**
	 * 
	 * @param storeHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchOp(long timestamp, S8User initiator,
			RepoMgDatabase storeHandler, 
			String repositoryAddress,
			String branchName, 
			long version, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.storeHandler = storeHandler;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchName;
		this.version = version;
		this.onSucceed = onSucceed;
	}

	@Override
	public H3MgHandler<RepoMgStore> getHandler() {
		return storeHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<RepoMgStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<RepoMgStore>(storeHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoMgStore store) throws JOOS_CompilingException, IOException {
				MgRepositoryHandler repoHandler = store.getRepositoryHandler(repositoryAddress);

				if(repoHandler != null) {
					repoHandler.cloneBranch(timeStamp, initiator, branchId, version, onSucceed, options);
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
