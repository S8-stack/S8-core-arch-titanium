package com.s8.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessSpaceOp extends RequestDbMgOperation<SpaceMgStore> {




	/**
	 * space-handler
	 */
	public final SpaceMgDatabase spaceHandler;

	

	/**
	 * space-id
	 */
	public final String spaceId;



	/**
	 * on-processed
	 */
	public final MgCallback<SpaceExposureS8AsyncOutput> onProcessed;



	/**
	 * 
	 * @param handler
	 * @param onProcessed
	 * @param onFailed
	 */
	public AccessSpaceOp(long timestamp, S8User initiator, SpaceMgDatabase handler, 
			String repositoryAddress, 
			MgCallback<SpaceExposureS8AsyncOutput> onProcessed, 
			long options) {
		super(timestamp, initiator, options);
		this.spaceHandler = handler;
		this.spaceId = repositoryAddress;
		this.onProcessed = onProcessed;
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


				MgSpaceHandler spaceHandler = store.getSpaceHandler(spaceId);

				if(spaceHandler != null) {
					/* exit point 1 -> continue */
					spaceHandler.accessSpace(timeStamp, initiator, onProcessed, options);
				}
				else {
					/* exit point 2 -> soft fail */
					SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
					output.isSuccessful = false;
					output.isSpaceDoesNotExist = true;
					onProcessed.call(output);
				}
				
				/* no new space created */
				return false;
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
