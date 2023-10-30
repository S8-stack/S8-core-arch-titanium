package com.s8.arch.magnesium.databases.repository.entry;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.RepositoryMetadataS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class GetMetadataOp extends RequestDbMgOperation<MgRepository> {


	public final MgRepositoryHandler repoHandler;

	public final MgCallback<RepositoryMetadataS8AsyncOutput> onSucceed;



	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public GetMetadataOp(long timestamp, S8User initiator,
			MgRepositoryHandler repoHandler, 
			MgCallback<RepositoryMetadataS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.repoHandler = repoHandler;
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
				return "METADATA of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepository repository) {
				RepositoryMetadataS8AsyncOutput output = new RepositoryMetadataS8AsyncOutput();
				output.isSuccessful = true;
				output.metadata = repository.metadata;
 				onSucceed.call(output);
 				return false;
			}

			@Override
			public void catchException(Exception exception) {
				RepositoryMetadataS8AsyncOutput output = new RepositoryMetadataS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}			
		};
	}


}
