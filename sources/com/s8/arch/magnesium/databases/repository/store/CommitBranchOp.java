package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.types.JOOS_CompilingException;


/**
 * 
 * @author pierreconvert
 *
 */
class CommitBranchOp extends RequestDbMgOperation<MgRepoStore> {




	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;

	public final String branchId;

	public final NdObject[] objects;

	public final String comment;
	
	public final MgCallback<BranchVersionS8AsyncOutput> onSucceed;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitBranchOp(long timestamp, S8User initiator,
			RepoMgDatabase handler, 
			String repositoryAddress,
			String branchId, 
			NdObject[] objects, 
			String comment,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.storeHandler = handler;
		this.repositoryAddress = repositoryAddress;
		this.branchId = branchId;
		this.objects = objects;
		this.comment = comment;
		this.onSucceed = onSucceed;
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
				return "COMMIT-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}
			

			@Override
			public boolean consumeResource(MgRepoStore store) throws JOOS_CompilingException, IOException {

				MgRepositoryHandler repoHandler = store.getRepositoryHandler(repositoryAddress);

				if(repoHandler != null) {
					repoHandler.commitBranch(timeStamp, initiator, branchId, objects, comment, onSucceed, options);
					return true;
				}
				else {
					BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
					output.isSuccessful = false;
					output.isRepositoryDoesNotExist = true;
					onSucceed.call(output);
					return false;
				}
			}

			@Override
			public void catchException(Exception exception) {
				BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}		
		};
	}




}
