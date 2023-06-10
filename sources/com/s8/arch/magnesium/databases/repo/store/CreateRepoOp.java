package com.s8.arch.magnesium.databases.repo.store;

import com.s8.arch.fluor.outputs.RepoCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repo.repository.MgRepositoryHandler;
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
class CreateRepoOp extends RequestH3MgOperation<MgRepoStore> {


	public final RepoMgDatabase handler;

	public final String repositoryAddress;

	public final MgCallback<RepoCreationS8AsyncOutput> onSucceed;

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
	public CreateRepoOp(long timestamp, 
			RepoMgDatabase handler, 
			String repositoryAddress,
			MgCallback<RepoCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
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

					MgRepositoryHandler existingRepoHandler = store.getRepositoryHandler(repositoryAddress, false);

					if(existingRepoHandler != null) {
						RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
						output.isSuccessful = false;
						output.hasNameConflict = true;
						onSucceed.call(output);
					}
					else {
						store.getRepositoryHandler(repositoryAddress, true);
						
						RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
						output.isSuccessful = true;
						output.hasNameConflict = false;
						onSucceed.call(output);
					}
				}
				catch(Exception exception) { 
					RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
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
				RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}



}
