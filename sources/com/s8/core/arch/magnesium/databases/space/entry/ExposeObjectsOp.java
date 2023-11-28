package com.s8.core.arch.magnesium.databases.space.entry;

import com.s8.api.bytes.Bool64;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceVersionS8AsyncOutput;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.core.arch.magnesium.callbacks.MgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.bohr.lithium.branches.LiBranch;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends RequestDbMgOperation<LiBranch> {




	/**
	 * 
	 */
	public final MgSpaceHandler spaceHandler;


	public final Object[] objects;

	/**
	 * 
	 */
	public final MgCallback<SpaceVersionS8AsyncOutput> onExposed;




	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, S8User initiator, MgSpaceHandler spaceHandler, 
			Object[] objects,
			MgCallback<SpaceVersionS8AsyncOutput> onExposed, 
			long options) {
		super(timestamp, initiator, options);
		this.spaceHandler = spaceHandler;
		this.objects = objects;
		this.onExposed = onExposed;
	}

	@Override
	public MgSpaceHandler getHandler() {
		return spaceHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<LiBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<LiBranch>(spaceHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+spaceHandler.getIdentifier()+" branch of "+spaceHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(LiBranch branch) throws S8IOException {
				SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();


				/* ranges */
				int range = objects.length;
				for(int slot = 0; slot < range; slot++) {
					branch.expose(slot, (SpaceS8Object) objects[slot]);	
				}

				output.version = 0x0L; // TODO

				onExposed.call(output);

				if(Bool64.has(options, S8AsyncFlow.SAVE_IMMEDIATELY_AFTER)) {
					handler.save();
				}

				return true;
			}

			@Override
			public void catchException(Exception exception) {
				SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
				output.reportException(exception);
				onExposed.call(output);
			}

		};
	}


}
