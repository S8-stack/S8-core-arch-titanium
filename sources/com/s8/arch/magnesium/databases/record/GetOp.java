package com.s8.arch.magnesium.databases.record;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.GetUserS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.object.BeObject;

public class GetOp extends RequestH3MgOperation<BeBranch> {

	public final RecordsMgDatabase dbHandler;

	public final String key;

	public final MgCallback<GetUserS8AsyncOutput> onRetrieved;

	public final long options;

	//public final ExceptionMgCallback onFailed;

	public GetOp(long timeStamp, RecordsMgDatabase dbHandler, 
			String key, 
			MgCallback<GetUserS8AsyncOutput> onRetrieved, 
			long options) {
		super(timeStamp);
		this.dbHandler = dbHandler;
		this.key = key;
		this.onRetrieved = onRetrieved;
		this.options = options;
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

				BeObject object =  (BeObject) branch.get(key);
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
