package com.s8.arch.magnesium.databases.repository.entry;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.BranchVersionS8AsyncOutput;
import com.s8.api.objects.repo.RepoS8Object;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
class CommitBranchOp extends RequestDbMgOperation<MgRepository> {


	public final MgRepositoryHandler reporHandler;

	public final String branchId;

	public final RepoS8Object[] objects;
	
	public final String comment;

	public final MgCallback<BranchVersionS8AsyncOutput> onSucceed;


	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitBranchOp(long timestamp, S8User initiator,
			MgRepositoryHandler reporHandler, String branchId, 
			RepoS8Object[] objects, String comment,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.reporHandler = reporHandler;
		this.branchId = branchId;
		this.objects = objects;
		this.comment = comment;
		this.onSucceed = onSucceed;
	}

	@Override
	public H3MgHandler<MgRepository> getHandler() {
		return reporHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<MgRepository> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<MgRepository>(reporHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "COMMIT-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {

				MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
				if(branchHandler != null) {
					// commit on branch
					branchHandler.commitBranch(timeStamp, initiator, objects, comment, onSucceed, options);
					return true;
				}
				else {
					BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
					output.isSuccessful = false;
					output.isBranchDoesNotExist = true;
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
