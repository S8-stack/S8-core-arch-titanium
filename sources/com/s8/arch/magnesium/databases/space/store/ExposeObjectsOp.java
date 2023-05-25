package com.s8.arch.magnesium.databases.space.store;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class ExposeObjectsOp extends UserH3MgOperation<MgS1Store> {


	
	public @Override boolean isModifyingResource() { return false; }
	
	
	/**
	 * 
	 */
	public final LithiumMgDatabase handler;
	
	
	public final String spaceId;
	
	public final Object[] objects;
	
	
	/**
	 * 
	 */
	public final VersionMgCallback onSucceed;
	
	
	/**
	 * 
	 */
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public ExposeObjectsOp(long timestamp, LithiumMgDatabase handler, 
			String spaceId,
			Object[] objects,
			VersionMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.spaceId = spaceId;
		this.objects = objects;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<MgS1Store> createConsumeResourceTask(MgS1Store store) {
		return new ConsumeResourceMgTask<MgS1Store>(store) {

			@Override
			public LithiumMgDatabase getHandler() {
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
			public void consumeResource(MgS1Store store) {
				try {
					store.getSpaceHandler(spaceId).exposeObjects(timeStamp, objects, onSucceed, onFailed);
				}
				catch(Exception exception) { onFailed.call(exception); }
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
				onFailed.call(exception);
			}
		};
	}


	
}
