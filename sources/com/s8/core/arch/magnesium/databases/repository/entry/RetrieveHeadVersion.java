package com.s8.core.arch.magnesium.databases.repository.entry;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.BranchVersionS8AsyncOutput;
import com.s8.core.arch.magnesium.callbacks.MgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class RetrieveHeadVersion extends RequestDbMgOperation<MgRepository> {


	public final MgRepositoryHandler repoHandler;

	public final String branchId;

	public final MgCallback<BranchVersionS8AsyncOutput> onSucceed;



	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public RetrieveHeadVersion(long timestamp, S8User initiator,
			MgRepositoryHandler repoHandler, 
			String branchId,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.repoHandler = repoHandler;
		this.branchId = branchId;
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
				return "CLONE-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {
				try {
					MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
					if(branchHandler != null) { 
						branchHandler.retrieveHeadVersion(timeStamp, initiator, onSucceed, options);
					}
					else {
						BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
						output.isBranchDoesNotExist = true;
						onSucceed.call(output);
					}
				}
				catch(Exception exception) {
					BranchVersionS8AsyncOutput output = new BranchVersionS8AsyncOutput();
					output.reportException(exception);
					onSucceed.call(output);
				}
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
