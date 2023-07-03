package com.s8.arch.magnesium.databases.repository.entry;

import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;


/**
 * 
 * @author pierreconvert
 *
 */
class CommitOp extends RequestH3MgOperation<MgRepository> {


	public final MgRepositoryHandler reporHandler;

	public final String branchId;

	public final NdObject[] objects;


	public final MgCallback<BranchVersionS8AsyncOutput> onSucceed;

	public final long options;


	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitOp(long timestamp,
			MgRepositoryHandler reporHandler, String branchId, NdObject[] objects, 
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.reporHandler = reporHandler;
		this.branchId = branchId;
		this.objects = objects;
		this.onSucceed = onSucceed;
		this.options = options;
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
					branchHandler.commit(timeStamp, objects, onSucceed, options);
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
