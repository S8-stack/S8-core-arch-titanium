package com.s8.core.arch.magnesium.databases.space.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceVersionS8AsyncOutput;
import com.s8.core.arch.magnesium.callbacks.MgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends RequestDbMgOperation<SpaceMgStore> {

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
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, S8User initiator, SpaceMgDatabase spaceHandler, 
			String spaceId,
			Object[] objects,
			MgCallback<SpaceVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.spaceHandler = spaceHandler;
		this.spaceId = spaceId;
		this.objects = objects;
		this.onSucceed = onSucceed;
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

				MgSpaceHandler spaceHandler = store.getSpaceHandler(spaceId);
				if(spaceHandler != null) {
					spaceHandler.exposeObjects(timeStamp, initiator, objects, onSucceed, options);

					/* not change in the db itself, despite space will be modified */
					return false;
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
