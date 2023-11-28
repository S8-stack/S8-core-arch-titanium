package com.s8.core.arch.magnesium.databases.record;

import com.s8.api.bytes.Bool64;
import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.outputs.PutUserS8AsyncOutput;
import com.s8.api.objects.table.TableS8Object;
import com.s8.core.arch.magnesium.callbacks.MgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.beryllium.branch.BeBranch;


/**
 * 
 * @author pierreconvert
 *
 */
public class PutOp extends RequestDbMgOperation<BeBranch> {


	public final RecordsMgDatabase dbHandler;

	public final TableS8Object object;

	public final MgCallback<PutUserS8AsyncOutput> onInserted;


	public PutOp(long timeStamp, RecordsMgDatabase dbHandler, TableS8Object object, 
			MgCallback<PutUserS8AsyncOutput> onInserted, 
			long options) {
		super(timeStamp, null, options);
		this.dbHandler = dbHandler;
		this.object = object;
		this.onInserted = onInserted;
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
				
				/* run function */
				onInserted.call(output);
				
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
