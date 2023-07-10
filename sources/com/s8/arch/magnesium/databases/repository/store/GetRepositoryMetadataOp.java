package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.RepositoryMetadataS8AsyncOutput;
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
class GetRepositoryMetadataOp extends RequestDbMgOperation<MgRepoStore> {





	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final MgCallback<RepositoryMetadataS8AsyncOutput> onDone;



	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public GetRepositoryMetadataOp(long timestamp, S8User initiator,
			RepoMgDatabase handler, 
			String repositoryAddress,
			MgCallback<RepositoryMetadataS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.storeHandler = handler;
		this.repositoryAddress = repositoryAddress;
		this.onDone = onSucceed;
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
				return "GET-META on "+repositoryAddress+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepoStore store) throws JOOS_CompilingException, IOException {
				MgRepositoryHandler repoHandler = store.getRepositoryHandler(repositoryAddress);
				if(repoHandler != null) {
					repoHandler.getRepositoryMetadata(timeStamp, initiator, onDone, options);
				}
				else {
					RepositoryMetadataS8AsyncOutput output = new RepositoryMetadataS8AsyncOutput();
					output.isSuccessful = false;
					output.isRepositoryDoesNotExist = true;
					onDone.call(output);
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				RepositoryMetadataS8AsyncOutput output = new RepositoryMetadataS8AsyncOutput();
				output.reportException(exception);
				onDone.call(output);
			}			
		};
	}

}
