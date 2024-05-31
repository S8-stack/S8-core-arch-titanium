package com.s8.core.arch.titanium.handlers.h2;

import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class DryShuttingDownTask<M> extends H2Task<M> {


	@Override
	public MthProfile profile() {
		return MthProfile.IO_DATA_LAKE;
	}

	@Override
	public String describe() {
		return "Shutting-DOWN : "+handle.describe();
	}

	

	/**
	 * 
	 * @param handle
	 */
	public DryShuttingDownTask(H2Handle<M> handle) {
		super(handle);
	}


	@Override
	public void run() {

		if(handle.isVerbose) {
			System.out.println("Starting DRY SHUTTING-DOWN sequence");
		}

		// switch state
		handle.terminate();
	}
}
