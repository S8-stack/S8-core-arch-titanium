package com.s8.arch.magnesium.databases.space.space;

import com.s8.arch.fluor.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.object.LiObject;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends UserH3MgOperation<LiBranch> {


	
	public @Override boolean isModifyingResource() { return true; }
	
	
	/**
	 * 
	 */
	public final MgSpaceHandler handler;
	
	
	public final Object[] objects;
	
	/**
	 * 
	 */
	public final MgCallback<SpaceVersionS8AsyncOutput> onExposed;
	
	
	/**
	 * 
	 */
	public final long options;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, MgSpaceHandler handler, 
			Object[] objects,
			MgCallback<SpaceVersionS8AsyncOutput> onExposed, 
			long options) {
		super(timestamp);
		this.handler = handler;
		this.objects = objects;
		this.onExposed = onExposed;
		this.options = options;
	}
	

	@Override
	public ConsumeResourceMgTask<LiBranch> createConsumeResourceTask(LiBranch branch) {
		return new ConsumeResourceMgTask<LiBranch>(branch) {

			@Override
			public MgSpaceHandler getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+handler.getIdentifier()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(LiBranch branch) {
				SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
				try {
					
					/* ranges */
					int range = objects.length;
					for(int slot = 0; slot < range; slot++) {
						branch.expose(slot, (LiObject) objects[slot]);	
					}
					
					output.version = 0x0L; // TODO
				}
				catch(Exception exception) { 
					output.reportException(exception);
				}
				onExposed.call(output);
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
				return "failed to access resource on "+handler.getName()+": catching exception";
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
