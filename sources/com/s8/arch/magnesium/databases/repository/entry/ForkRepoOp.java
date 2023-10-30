package com.s8.arch.magnesium.databases.repository.entry;

import java.util.HashMap;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.arch.magnesium.databases.repository.store.RepoMgStore;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
class ForkRepoOp extends RequestDbMgOperation<MgRepository> {


	/**
	 * origin repo handler
	 */
	public final MgRepositoryHandler repoHandler;


	public final String originBranchId;
	
	public final long originBranchVersion;

	/**
	 * target repo handler
	 */
	public final MgRepositoryHandler targetRepositoryHandler;
	
	public final String targetRepositoryName;


	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;

	public final RepoMgStore store;

	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkRepoOp(long timestamp, S8User initiator,
			MgRepositoryHandler repoHandler, 
			String originBranchId,
			long originBranchVersion,
			MgRepositoryHandler targetRepositoryHandler,
			String targetRepositoryName,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.repoHandler = repoHandler;
		this.originBranchId = originBranchId;
		this.originBranchVersion = originBranchVersion;
		this.targetRepositoryHandler = targetRepositoryHandler;
		this.targetRepositoryName = targetRepositoryName;
		this.onSucceed = onSucceed;

		this.store = repoHandler.store;
	}


	@Override
	public H3MgHandler<MgRepository> getHandler() {
		return repoHandler;
	}


	public String getOriginBranchId() {
		return repoHandler.address;
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
				return "COMMIT-HEAD on "+getOriginBranchId()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {

				MgRepositoryMetadata repoMetadata = repository.metadata.shallowClone();
				repoMetadata.name = targetRepositoryName;
				repoMetadata.branches = new HashMap<>();
				
				MgRepository targetRepository = new MgRepository(repoMetadata, targetRepositoryHandler.folderPath);



				MgBranchHandler originBranchHandler = repository.branchHandlers.get(originBranchId);
				if(originBranchHandler != null) {


					/* define a new (main) branch */
					MgBranchMetadata targetBranchMetadata = new MgBranchMetadata();
					targetBranchMetadata.name = originBranchId;
					targetBranchMetadata.info = "FORK from "+originBranchId+"["+originBranchVersion+"]";
					targetBranchMetadata.headVersion = 0L;
					targetBranchMetadata.forkedBranchId = originBranchId;
					targetBranchMetadata.forkedBranchVersion = originBranchVersion;
					
					repoMetadata.branches.put(originBranchId, targetBranchMetadata);


					MgBranchHandler targetBranchHandler = new MgBranchHandler(handler.ng, store, repository, targetBranchMetadata);
					targetRepository.branchHandlers.put(originBranchId, targetBranchHandler);

					originBranchHandler.forkBranch(timeStamp, initiator, originBranchVersion, targetBranchHandler, onSucceed, options);

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

			@Override
			public void catchException(Exception exception) {
				BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}			
		};

	}


}
