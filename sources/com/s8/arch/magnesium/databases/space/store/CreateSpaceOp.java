package com.s8.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.io.bohr.lithium.branches.LiBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class CreateSpaceOp extends RequestDbMgOperation<SpaceMgStore> {




	/**
	 * 
	 */
	public final SpaceMgDatabase spaceHandler;


	/**
	 * 
	 */
	public final String spaceId;
	
	/**
	 * 
	 */
	public final SpaceS8Object[] exposure;



	/**
	 * 
	 */
	public final MgCallback<SpaceExposureS8AsyncOutput> onProcessed;



	/**
	 * 
	 * @param handler
	 * @param onProcessed
	 * @param onFailed
	 */
	public CreateSpaceOp(long timestamp, S8User initiator, SpaceMgDatabase handler, 
			String spaceId, 
			SpaceS8Object[] exposure,
			MgCallback<SpaceExposureS8AsyncOutput> onProcessed, 
			long options) {
		super(timestamp, initiator, options);
		this.spaceHandler = handler;
		this.spaceId = spaceId;
		this.exposure = exposure;
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


				MgSpaceHandler spaceHandler = store.createSpaceHandler(spaceId);

				if(spaceHandler != null) {

					LiBranch branch = new LiBranch(spaceId, store.getCodebase());
					branch.expose(exposure);
					
					spaceHandler.initializeResource(branch);
					
					SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
					output.isSuccessful = true;
					output.objects = exposure;
					onProcessed.call(output);
					return true;
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
