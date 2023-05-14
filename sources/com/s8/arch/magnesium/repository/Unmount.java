package com.s8.arch.magnesium.repository;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VoidMgCallback;
import com.s8.arch.magnesium.shared.CatchExceptionMgTask;
import com.s8.arch.magnesium.shared.ConsumeResourceMgTask;
import com.s8.arch.magnesium.shared.MgOperation;
import com.s8.arch.magnesium.shared.MgSharedResourceHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;

public class Unmount extends MgOperation<MgRepository> {
	
	
	public final MgRepositoryHandler handler;
	
	public final VoidMgCallback onSucceed;
	
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public Unmount(MgRepositoryHandler handler, VoidMgCallback onSucceed, ExceptionMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}

	@Override
	public ConsumeResourceMgTask<MgRepository> createConsumeResourceTask(MgRepository repository) {
		return new ConsumeResourceMgTask<MgRepository>(repository) {
			
			@Override
			public MgSharedResourceHandler<MgRepository> getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+handler.id+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgRepository repository) {
				
				
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