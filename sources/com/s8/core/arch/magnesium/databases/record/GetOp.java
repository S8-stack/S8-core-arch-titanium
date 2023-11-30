package com.s8.core.arch.magnesium.databases.record;

import com.s8.api.flow.record.objects.RecordS8Object;
import com.s8.api.flow.record.requests.GetRecordS8Request;
import com.s8.api.flow.record.requests.GetRecordS8Request.Status;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.beryllium.branch.BeBranch;
import com.s8.core.bohr.beryllium.exception.BeIOException;

public class GetOp extends RequestDbMgOperation<BeBranch> {

	public final RecordsMgDatabase dbHandler;

	public final GetRecordS8Request request;
	
	
	public GetOp(long timeStamp, DbMgCallback callback, RecordsMgDatabase dbHandler, GetRecordS8Request request) {
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
			public boolean consumeResource(BeBranch branch) throws BeIOException {
				RecordS8Object object =  (RecordS8Object) branch.get(request.id);
				GetRecordS8Request.Status status = object != null ? Status.OK : Status.NOT_FOUND;
				request.onSucceed(status , object);
				callback.call();
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}

}
