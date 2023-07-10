package com.s8.arch.magnesium.databases.repository.branch;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class RetrieveHeadVersion extends RequestDbMgOperation<NdBranch> {


	public final MgBranchHandler branchHandler;

	public final MgCallback<BranchVersionS8AsyncOutput> onSucceed;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public RetrieveHeadVersion(long timestamp, S8User initiator, 
			MgBranchHandler branchHandler, 
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.branchHandler = branchHandler;
		this.onSucceed = onSucceed;
	}

	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return branchHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(branchHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchHandler.getIdentifier()+" branch of "+branchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) {
				BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();

				long version = branch.getHeadVersion();
				output.version = version;

				onSucceed.call(output);
				return false;
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
