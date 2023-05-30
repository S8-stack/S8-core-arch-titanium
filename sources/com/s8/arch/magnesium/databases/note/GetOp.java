package com.s8.arch.magnesium.databases.note;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.GetUserS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.object.BeObject;

public class GetOp extends UserH3MgOperation<BeBranch> {
	
	@Override
	public boolean isModifyingResource() {
		return false;
	}
	
	public final NoteMgDatabase handler;
	
	public final String key;
	
	public final MgCallback<GetUserS8AsyncOutput> onRetrieved;
	
	public final long options;
	
	//public final ExceptionMgCallback onFailed;

	public GetOp(long timeStamp, NoteMgDatabase handler, 
			String key, 
			MgCallback<GetUserS8AsyncOutput> onRetrieved, 
			long options) {
		super(timeStamp);
		this.handler = handler;
		this.key = key;
		this.onRetrieved = onRetrieved;
		this.options = options;
	}

	@Override
	public ConsumeResourceMgTask<BeBranch> createConsumeResourceTask(BeBranch branch) {
		return new ConsumeResourceMgTask<BeBranch>(branch) {

			@Override
			public String describe() {
				return "login op";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public H3MgHandler<BeBranch> getHandler() {
				return handler;
			}

			@Override
			public void consumeResource(BeBranch branch) {
				GetUserS8AsyncOutput output = new GetUserS8AsyncOutput();
				try {
					BeObject object =  (BeObject) branch.get(key);
					output.setUser((S8User) object);
					
				} 
				catch (BeIOException e) {
					e.printStackTrace();
					output.reportException(e);
				}
				onRetrieved.call(output);
			}
		};
	}

	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) {
		return new CatchExceptionMgTask(exception) {
			
			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}
			
			@Override
			public String describe() {
				return "catching exception";
			}
			
			@Override
			public void catchException(Exception exception) {
				GetUserS8AsyncOutput output = new GetUserS8AsyncOutput();
				output.reportException(exception);	
				onRetrieved.call(output);
			}
		};
	}

	

}
