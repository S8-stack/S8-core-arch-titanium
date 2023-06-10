package com.s8.arch.magnesium.databases.space.store;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.space.space.MgSpaceHandler;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bytes.alpha.Bool64;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends RequestH3MgOperation<SpaceMgStore> {


	
	public @Override boolean isModifyingResource() { return false; }
	
	
	/**
	 * 
	 */
	public final SpaceMgDatabase handler;
	
	
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
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, SpaceMgDatabase handler, 
			String spaceId,
			Object[] objects,
			MgCallback<SpaceVersionS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp);
		this.handler = handler;
		this.spaceId = spaceId;
		this.objects = objects;
		this.onSucceed = onSucceed;
		this.options = options;
	}
	

	@Override
	public ConsumeResourceMgTask<SpaceMgStore> createConsumeResourceTask(SpaceMgStore store) {
		return new ConsumeResourceMgTask<SpaceMgStore>(store) {

			@Override
			public SpaceMgDatabase getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "ACCESS-EXPOSURE on "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(SpaceMgStore store) {
				
				try {
					boolean isCreateOptionEnabled = Bool64.has(options, S8AsyncFlow.CREATE_SPACE_IF_NOT_PRESENT);
					MgSpaceHandler spaceHandler = store.resolveSpaceHandler(spaceId, isCreateOptionEnabled);
					if(spaceHandler != null) {
						spaceHandler.exposeObjects(timeStamp, objects, onSucceed, options);	
					}
					else {
						SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
						output.isSuccessful = false;
						output.isResourceUnavailable = true;
						onSucceed.call(output);
					}
				}
				catch(Exception exception) { 
					exception.printStackTrace();
					
					SpaceVersionS8AsyncOutput output = new SpaceVersionS8AsyncOutput();
					output.isSuccessful = false;
					output.reportException(exception);
					onSucceed.call(output);
				}
				
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
				onSucceed.call(output);
			}
		};
	}


	
}
