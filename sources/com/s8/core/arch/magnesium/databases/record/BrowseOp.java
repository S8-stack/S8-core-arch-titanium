package com.s8.core.arch.magnesium.databases.record;

import java.util.List;

import com.s8.api.flow.record.objects.RecordS8Object;
import com.s8.api.flow.record.requests.SelectRecordsS8Request;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.beryllium.branch.BeBranch;
import com.s8.core.bohr.beryllium.exception.BeIOException;


/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class BrowseOp<T extends RecordS8Object> extends RequestDbMgOperation<BeBranch> {



	/**
	 * handler
	 */
	public final RecordsMgDatabase dbHandler;


	/**
	 * 
	 */
	public final SelectRecordsS8Request<T> request;




	public BrowseOp(long timeStamp, DbMgCallback callback, RecordsMgDatabase dbHandler, SelectRecordsS8Request<T> request) {
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
				List<T> objects = branch.select(request.filter);
				request.onResponse(objects);
				callback.call();
				return false; // no resources modified
			}

			@Override
			public void catchException(Exception exception) {
				request.onError(exception);
				callback.call();
			}
		};
	}

}
