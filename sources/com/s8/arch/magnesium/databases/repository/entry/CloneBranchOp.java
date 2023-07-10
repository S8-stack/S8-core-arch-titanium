package com.s8.arch.magnesium.databases.repository.entry;

import java.io.IOException;

import com.s8.arch.fluor.S8User;
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
class CloneBranchOp extends RequestH3MgOperation<MgRepository> {


	public final MgRepositoryHandler repoHandler;

	public final String branchId;

	public final long version;

	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;

	public final long options;



	/**
	 * 
	 * @param storeHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchOp(long timestamp, S8User initator,
			MgRepositoryHandler repoHandler, String branchId, long version, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initator);
		this.repoHandler = repoHandler;
		this.branchId = branchId;
		this.version = version;
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
			public boolean consumeResource(MgRepository repository) throws IOException {

				MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);

				if(branchHandler == null) { 
					throw new IOException("No branch "+branchId+" on repo "+repository.getAddress()); 
				}
				branchHandler.cloneBranch(timeStamp, initiator, version, onSucceed, options);

				return false;
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
