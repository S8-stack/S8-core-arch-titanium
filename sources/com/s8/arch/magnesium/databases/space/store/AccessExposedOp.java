package com.s8.arch.magnesium.databases.space.store;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessExposedOp extends UserH3MgOperation<SpaceMgStore> {


	
	public @Override boolean isModifyingResource() { return false; }
	
	
	/**
	 * 
	 */
	public final SpaceMgDatabase handler;
	
	
	public final String repositoryAddress;
	
	public final int slot;
	
	
	/**
	 * 
	 */
	public final ObjectMgCallback onSucceed;
	
	
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
	public AccessExposedOp(long timestamp, SpaceMgDatabase handler, 
			String repositoryAddress, 
			int slot, 
			ObjectMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
		this.slot = slot;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
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
					store.getSpaceHandler(repositoryAddress).accessExposed(timeStamp, slot, onSucceed, onFailed);
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
