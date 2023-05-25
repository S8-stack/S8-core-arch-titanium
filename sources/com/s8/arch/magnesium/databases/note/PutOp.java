package com.s8.arch.magnesium.databases.note;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.object.BeObject;

public class PutOp extends UserH3MgOperation<BeBranch> {
	
	public final NoteMgDatabase handler;
	
	public final String key;
	
	public final BeObject object;
	
	public final BooleanMgCallback onInserted;
	
	public final ExceptionMgCallback onFailed;

	public PutOp(long timeStamp, NoteMgDatabase handler, String key, BeObject object, 
			BooleanMgCallback onInserted, ExceptionMgCallback onFailed) {
		super(timeStamp);
		this.handler = handler;
		this.key = key;
		this.object = object;
		this.onInserted = onInserted;
		this.onFailed = onFailed;
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
				try {
					BeObject object =  (BeObject) branch.get(key);
					branch.put(key, object);
					onInserted.call(true);
					
				} catch (BeIOException e) {
					e.printStackTrace();
					onFailed.call(e);
				}
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
				onFailed.call(exception);	
			}
		};
	}

	@Override
	public boolean isModifyingResource() {
		return false;
	}

}
