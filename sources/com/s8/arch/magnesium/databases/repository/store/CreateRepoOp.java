package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.arch.fluor.outputs.RepoCreationS8AsyncOutput;
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
class CreateRepoOp extends RequestH3MgOperation<MgRepoStore> {


	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final MgCallback<RepoCreationS8AsyncOutput> onSucceed;

	public final long options;



	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CreateRepoOp(long timestamp, 
			RepoMgDatabase handler, 
			String repositoryAddress,
			MgCallback<RepoCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.storeHandler = handler;
		this.repositoryAddress = repositoryAddress;
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


				MgRepositoryHandler existingRepoHandler = store.getRepositoryHandler(repositoryAddress, false);

				if(existingRepoHandler != null) {
					RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
					output.isSuccessful = false;
					output.hasNameConflict = true;
					onSucceed.call(output);
					return true;
				}
				else {
					store.getRepositoryHandler(repositoryAddress, true);

					RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
					output.isSuccessful = true;
					output.hasNameConflict = false;
					onSucceed.call(output);
					return false;
				}
			}

			@Override
			public void catchException(Exception exception) {
				RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}


}
