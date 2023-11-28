package com.s8.core.arch.magnesium.databases.record;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.GetUserS8AsyncOutput;
import com.s8.api.objects.table.TableS8Object;
import com.s8.core.arch.magnesium.callbacks.MgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.beryllium.branch.BeBranch;
import com.s8.core.bohr.beryllium.exception.BeIOException;

public class GetOp extends RequestDbMgOperation<BeBranch> {

	public final RecordsMgDatabase dbHandler;

	public final String key;

	public final MgCallback<GetUserS8AsyncOutput> onRetrieved;
	
	
	public GetOp(long timeStamp, RecordsMgDatabase dbHandler, 
			String key, 
			MgCallback<GetUserS8AsyncOutput> onRetrieved, 
			long options) {
		super(timeStamp, null, options);
		this.dbHandler = dbHandler;
		this.key = key;
		this.onRetrieved = onRetrieved;
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
				GetUserS8AsyncOutput output = new GetUserS8AsyncOutput();

				TableS8Object object =  (TableS8Object) branch.get(key);
				output.setUser((S8User) object);

				onRetrieved.call(output);
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				exception.printStackTrace();
				GetUserS8AsyncOutput output = new GetUserS8AsyncOutput();
				output.reportException(exception);
				onRetrieved.call(output);

			}
		};
	}

}
