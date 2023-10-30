package com.s8.arch.magnesium.databases.repository.entry;

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
class ForkBranchOp extends RequestDbMgOperation<MgRepository> {


	public final MgRepositoryHandler repoHandler;

	public final String originBranchId;

	public final long originBranchVersion;

	public final String targetBranchId;

	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;



	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkBranchOp(long timestamp, S8User initiator,
			MgRepositoryHandler repoHandler, 
			String originBranchId, long originBranchVersion, String targetBranchId,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.repoHandler = repoHandler;
		this.originBranchId = originBranchId;
		this.originBranchVersion = originBranchVersion;
		this.targetBranchId = targetBranchId;
		this.onSucceed = onSucceed;
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
				return "COMMIT-HEAD on "+originBranchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {

				MgBranchHandler originBranchHandler = repository.branchHandlers.get(originBranchId);
				if(originBranchHandler != null) {

					if(!repository.branchHandlers.containsKey(targetBranchId)) {

						/* define a new (main) branch */
						MgBranchMetadata targetBranchMetadata = new MgBranchMetadata();
						targetBranchMetadata.name = targetBranchId;
						targetBranchMetadata.owner = initiator.getUsername();
						targetBranchMetadata.info = "FORK from "+originBranchId+"["+originBranchVersion+"]";
						targetBranchMetadata.headVersion = 0L;
						targetBranchMetadata.forkedBranchId = originBranchId;
						targetBranchMetadata.forkedBranchVersion = originBranchVersion;

						/* add branch metadata to repo meta*/
						repository.metadata.branches.put(targetBranchId, targetBranchMetadata);

						RepoMgStore store = repoHandler.store;
						MgBranchHandler targetBranchHandler = new MgBranchHandler(handler.ng, store, repository, targetBranchMetadata);
						repository.branchHandlers.put(targetBranchId, targetBranchHandler);

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
