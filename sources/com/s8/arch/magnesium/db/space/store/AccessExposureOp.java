package com.s8.arch.magnesium.db.space.store;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class AccessExposureOp extends UserH3MgOperation<MgS1Store> {


	
	public @Override boolean isModifyingResource() { return false; }
	
	
	/**
	 * 
	 */
	public final MgDb1StoreHandler handler;
	
	
	public final String repositoryAddress;
	
	
	
	/**
	 * 
	 */
	public final ObjectsMgCallback onSucceed;
	
	
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
	public AccessExposureOp(long timestamp, MgDb1StoreHandler handler, 
			String repositoryAddress, 
			ObjectsMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.repositoryAddress = repositoryAddress;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<MgS1Store> createConsumeResourceTask(MgS1Store store) {
		return new ConsumeResourceMgTask<MgS1Store>(store) {

			@Override
			public MgDb1StoreHandler getHandler() {
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
					store.getSpaceHandler(repositoryAddress).accessExposure(timeStamp, onSucceed, onFailed);
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
