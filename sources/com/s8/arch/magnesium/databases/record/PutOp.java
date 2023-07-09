package com.s8.arch.magnesium.databases.record;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.PutUserS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bytes.alpha.Bool64;


/**
 * 
 * @author pierreconvert
 *
 */
public class PutOp extends RequestH3MgOperation<BeBranch> {


	public final RecordsMgDatabase dbHandler;

	public final BeObject object;

	public final MgCallback<PutUserS8AsyncOutput> onInserted;

	public final long options;

	public PutOp(long timeStamp, RecordsMgDatabase dbHandler, BeObject object, 
			MgCallback<PutUserS8AsyncOutput> onInserted, 
			long options) {
		super(timeStamp);
		this.dbHandler = dbHandler;
		this.object = object;
		this.onInserted = onInserted;
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
			public boolean consumeResource(BeBranch branch) throws Exception {
				PutUserS8AsyncOutput output = new PutUserS8AsyncOutput();
				boolean hasBeenModified = false;

				String key = object.S8_key;
				
				boolean isCheckingOverride = Bool64.has(options, S8AsyncFlow.SHOULD_NOT_OVERRIDE);
				
				if(!isCheckingOverride) {
					branch.put(object);
					hasBeenModified = true;
					output.isSuccessful = true;
				}
				else {
					if(!branch.hasEntry(key)) {
						branch.put(object);
						hasBeenModified = true;
						output.isSuccessful = true;
					}
					else {
						output.isSuccessful = false;
						output.hasIdConflict = true;
					}
				}

				if(hasBeenModified && Bool64.has(options, S8AsyncFlow.SAVE_IMMEDIATELY_AFTER)) {
					handler.save();
				}
				return hasBeenModified;
			}

			@Override
			public void catchException(Exception exception) {
				PutUserS8AsyncOutput output = new PutUserS8AsyncOutput();
				output.reportException(exception);
				onInserted.call(output);
			}
		};
	}
}
