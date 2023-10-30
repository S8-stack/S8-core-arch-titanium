package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class ForkRepoOp extends RequestDbMgOperation<RepoMgStore> {


	/**
	 * 
	 */
	public final RepoMgDatabase storeHandler;

	
	/**
	 * 
	 */
	public final String originRepositoryAddress;
	
	
	public final String originBranchId;
	
	
	public final long originBranchVersion;
	
	

	/**
	 * 
	 */
	public final String targetRepositoryName;
	
	
	/**
	 * 
	 */
	public final String targetRepositoryAddress;

	

	
	/**
	 * 
	 */
	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;



	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkRepoOp(long timestamp, S8User initiator,
			RepoMgDatabase handler, 
			String originRepositoryAddress,
			String originBranchId, long originBranchVersion,
			String targetRepositoryName, String targetRepositoryAddress,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);

		/* fields */
		this.storeHandler = handler;
		this.originRepositoryAddress = originRepositoryAddress;
		this.originBranchId = originBranchId;
		this.originBranchVersion = originBranchVersion;
		
		this.targetRepositoryName = targetRepositoryName;
		this.targetRepositoryAddress = targetRepositoryAddress;
		
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
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoMgStore store) throws JOOS_CompilingException, IOException {

				MgRepositoryHandler originRepoHandler = store.getRepositoryHandler(originRepositoryAddress);
				if(originRepoHandler != null) {
					
					MgRepositoryHandler targetRepoHandler = store.createRepositoryHandler(targetRepositoryAddress);
					if(targetRepositoryAddress != null) {
						originRepoHandler.forkRepo(timeStamp, initiator, 
								originBranchId, originBranchVersion, 
								targetRepoHandler, targetRepositoryName,
								onSucceed, options);
					}	
					else {
						BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
						output.isSuccessful = false;
						output.isRepositoryDoesNotExist = true;
						onSucceed.call(output);
					}
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
