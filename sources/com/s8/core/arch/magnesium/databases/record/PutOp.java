package com.s8.core.arch.magnesium.databases.record;

import com.s8.api.flow.record.requests.PutRecordS8Request;
import com.s8.api.flow.record.requests.PutRecordS8Request.Status;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.beryllium.branch.BeBranch;


/**
 * 
 * @author pierreconvert
 *
 */
public class PutOp extends RequestDbMgOperation<BeBranch> {


	public final RecordsMgDatabase dbHandler;

	public final PutRecordS8Request request;


	public PutOp(long timeStamp, SiliconChainCallback callback, RecordsMgDatabase dbHandler, PutRecordS8Request request) {
		super(timeStamp, null, callback);
		this.dbHandler = dbHandler;
		this.request = request;
	}


	@Override
	public H3MgHandler<BeBranch> getHandler() {
		return dbHandler;
	}



	@Override
	public ConsumeResourceMgAsyncTask<BeBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<BeBranch>(dbHandler) {

			@Override
			public String describe() {
				return "login op";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public boolean consumeResource(BeBranch branch) throws Exception {
				
				boolean hasBeenModified = false;
				PutRecordS8Request.Status status = Status.OK;

				String key = request.record.S8_key;
				
				
				if(request.isOverridingAllowed) {
					branch.put(request.record);
					status = Status.OK;
					hasBeenModified = true;
				}
				else { // no overriding
					if(!branch.hasEntry(key)) {
						branch.put(request.record);
						status = Status.OK;
						hasBeenModified = true;
					}
					else {
						status = Status.ID_CONFLICT;
						hasBeenModified = false;
					}
				}

				if(hasBeenModified && request.isImmediateHDWriteRequired) {
					handler.save();
				}
				
				/* run function */
				request.onResponse(status);
				callback.call();
				
				return hasBeenModified;
			}

			@Override
			public void catchException(Exception exception) {
				request.onError(exception);
				callback.call();
			}
		};
	}
}
