package com.s8.arch.magnesium.databases.space.store;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
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
class AccessExposureOp extends RequestH3MgOperation<SpaceMgStore> {



	public @Override boolean isModifyingResource() { return false; }


	/**
	 * 
	 */
	public final SpaceMgDatabase handler;


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
		this.handler = handler;
		this.spaceId = repositoryAddress;
		this.onProcessed = onProcessed;
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
				return MthProfile.IO_SSD; 
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
						/* exit point 1 -> continue */
						spaceHandler.accessExposure(timeStamp, onProcessed, options);
					}
					else {
						
						/* exit point 2 -> soft fail */
						SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
						output.isSuccessful = false;
						output.isSpaceDoesNotExist = true;
						onProcessed.call(output);
					}				
				}
				catch(Exception exception) {
					exception.printStackTrace();
					
					/* exit point 3 -> hard fail */
					SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
					output.reportException(exception);
					onProcessed.call(output);
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
				SpaceExposureS8AsyncOutput output = new SpaceExposureS8AsyncOutput();
				output.reportException(exception);
				onProcessed.call(output);
			}
		};
	}
}
