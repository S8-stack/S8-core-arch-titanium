package com.s8.core.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request.Status;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.joos.types.JOOS_CompilingException;


/**
 * 
 * @author pierreconvert
 *
 */
class CommitBranchOp extends RequestDbMgOperation<RepoMgStore> {




	public final RepoMgDatabase storeHandler;

	public final CommitBranchS8Request request;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CommitBranchOp(long timestamp, S8User initiator, DbMgCallback callback, 
			RepoMgDatabase handler, CommitBranchS8Request request) {
		super(timestamp, initiator, callback);
		this.storeHandler = handler;
		this.request = request;
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
				return "COMMIT-HEAD on "+request.branchId+" branch of "+handler.getName()+ " repository";
			}
			

			@Override
			public boolean consumeResource(RepoMgStore store) throws JOOS_CompilingException, IOException {

				MgRepositoryHandler repoHandler = store.getRepositoryHandler(request.repositoryAddress);

				if(repoHandler != null) {
					repoHandler.commitBranch(timeStamp, initiator, callback, request);
					return true;
				}
				else {
					request.onResponse(Status.REPOSITORY_NOT_FOUND, 0x0L);
					callback.call();
					return false;
				}
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}		
		};
	}




}
