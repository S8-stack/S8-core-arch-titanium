package com.s8.arch.magnesium.databases.note;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.PutUserS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bytes.alpha.Bool64;

public class PutOp extends UserH3MgOperation<BeBranch> {
	
	public final NoteMgDatabase handler;
	
	public final String key;
	
	public final BeObject object;
	
	public final MgCallback<PutUserS8AsyncOutput> onInserted;
	
	public final long options;

	public PutOp(long timeStamp, NoteMgDatabase handler, String key, BeObject object, 
			MgCallback<PutUserS8AsyncOutput> onInserted, 
			long options) {
		super(timeStamp);
		this.handler = handler;
		this.key = key;
		this.object = object;
		this.onInserted = onInserted;
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
				PutUserS8AsyncOutput output = new PutUserS8AsyncOutput();
				try {
					
					if(!branch.hasEntry(key)) {
						branch.put(key, object);
						
						if(Bool64.has(options, S8AsyncFlow.SAVE_IMMEDIATELY_AFTER)) {
							handler.save();
						}
						
						output.isSuccessful = true;
					}
					else {
						output.hasIdConflict = true;
					}
				} catch (BeIOException e) {
					e.printStackTrace();
					output.reportException(e);
				}
				onInserted.call(output);
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
				PutUserS8AsyncOutput output = new PutUserS8AsyncOutput();
				output.reportException(exception);
				onInserted.call(output);
			}
		};
	}

	@Override
	public boolean isModifyingResource() {
		return false;
	}

}
