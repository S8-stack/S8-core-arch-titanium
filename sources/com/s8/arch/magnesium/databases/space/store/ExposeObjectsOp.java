package com.s8.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bytes.alpha.Bool64;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends RequestH3MgOperation<SpaceMgStore> {

	/**
	 * 
	 */
	public final SpaceMgDatabase spaceHandler;


	public final String spaceId;

	public final Object[] objects;


	/**
	 * 
	 */
	public final MgCallback<SpaceVersionS8AsyncOutput> onSucceed;


	/**
	 * 
	 */
	public final long options;


	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, SpaceMgDatabase spaceHandler, 
			String spaceId,
			Object[] objects,
			MgCallback<SpaceVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.spaceHandler = spaceHandler;
		this.spaceId = spaceId;
		this.objects = objects;
		this.onSucceed = onSucceed;
		this.options = options;
	}

	@Override
	public SpaceMgDatabase getHandler() {
		return spaceHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<SpaceMgStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<SpaceMgStore>(spaceHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "ACCESS-EXPOSURE on "+spaceHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(SpaceMgStore store) throws IOException {
				
				boolean isCreateOptionEnabled = Bool64.has(options, S8AsyncFlow.CREATE_SPACE_IF_NOT_PRESENT);
				MgSpaceHandler spaceHandler = store.resolveSpaceHandler(spaceId, isCreateOptionEnabled);
				if(spaceHandler != null) {
					spaceHandler.exposeObjects(timeStamp, objects, onSucceed, options);

					if(spaceHandler.isNewlyCreated) {
						if(Bool64.has(options, S8AsyncFlow.SAVE_IMMEDIATELY_AFTER)) {
							handler.save();
						}
						spaceHandler.isNewlyCreated = false; // clear flag
						return true;
					}
					else {
						/* not change in the db itself, despite space will be modified */
						return false;
					}
				}
				else {
					SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
					output.isSuccessful = false;
					output.isResourceUnavailable = true;
					onSucceed.call(output);
					return false;
				}
			}
			

			@Override
			public void catchException(Exception exception) {
				exception.printStackTrace();

				SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
				output.isSuccessful = false;
				output.reportException(exception);
				onSucceed.call(output);

			}
		};
	}



}
