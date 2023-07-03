package com.s8.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
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
class AccessExposureOp extends RequestH3MgOperation<SpaceMgStore> {




	/**
	 * 
	 */
	public final SpaceMgDatabase spaceHandler;


	public final String spaceId;



	/**
	 * 
	 */
	public final MgCallback<SpaceExposureS8AsyncOutput> onProcessed;


	public final long options;


	/**
	 * 
	 * @param handler
	 * @param onProcessed
	 * @param onFailed
	 */
	public AccessExposureOp(long timestamp, SpaceMgDatabase handler, 
			String repositoryAddress, 
			MgCallback<SpaceExposureS8AsyncOutput> onProcessed, 
			long options) {
		super(timestamp);
		this.spaceHandler = handler;
		this.spaceId = repositoryAddress;
		this.onProcessed = onProcessed;
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
				return MthProfile.IO_SSD; 
			}

			@Override
			public String describe() {
				return "ACCESS-EXPOSURE on "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(SpaceMgStore store) throws IOException {


				boolean isCreateOptionEnabled = Bool64.has(options, S8AsyncFlow.CREATE_SPACE_IF_NOT_PRESENT);
				MgSpaceHandler spaceHandler = store.resolveSpaceHandler(spaceId, isCreateOptionEnabled);

				if(spaceHandler != null) {
					/* exit point 1 -> continue */
					spaceHandler.accessExposure(timeStamp, onProcessed, options);

					if(spaceHandler.isNewlyCreated) {
						if(Bool64.has(options, S8AsyncFlow.SAVE_IMMEDIATELY_AFTER)) {
							handler.save();
						}
						spaceHandler.isNewlyCreated = false; // clear flag
						return true;
					}
					else {
						return false;
					}
				}
				else {

					/* exit point 2 -> soft fail */
					SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
					output.isSuccessful = false;
					output.isSpaceDoesNotExist = true;
					onProcessed.call(output);
					return false;
				}
			}

			@Override
			public void catchException(Exception exception) {

				exception.printStackTrace();

				/* exit point 3 -> hard fail */
				SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
				output.reportException(exception);
				onProcessed.call(output);
			}
		};
	}

}
