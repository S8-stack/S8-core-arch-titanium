package com.s8.arch.magnesium.databases.repository.entry;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends RequestH3MgOperation<MgRepository> {




	public final MgRepositoryHandler repoHandler;

	public final String branchId;

	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;

	public final long options;


	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgRepositoryHandler repoHandler, String branchId, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.repoHandler = repoHandler;
		this.branchId = branchId;
		this.onSucceed = onSucceed;
		this.options = options;
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
				return "CLONE-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {

				MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
				if(branchHandler != null) {
					branchHandler.cloneHead(timeStamp, onSucceed, options);
					return false;
				}
				else {
					BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
					output.isSuccessful = false;
					output.isBranchDoesNotExist = true;
					onSucceed.call(output);
					return false;
				}
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
