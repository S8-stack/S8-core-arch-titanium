package com.s8.core.arch.titanium.handlers.h2;

import java.io.IOException;

import com.s8.core.arch.silicon.SiException;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.handlers.h2.H2Handle.State;


/**
 * 
 * @author pierreconvert
 *
 */
public class LoadingTask<M> extends H2Task<M> {



	@Override
	public String describe() {
		return "LOADING for handle: "+handle.describe();
	}


	@Override
	public MthProfile profile() {
		return MthProfile.IO_DATA_LAKE;
	}



	/**
	 * 
	 * @param handle
	 */
	public LoadingTask(H2Handle<M> handle) {
		super(handle);
	}


	@Override
	public void run() {
		if(handle.isVerbose) {
			System.out.println("[H2Handle.LoadingTask] Start reading...");
		}

		try {

			handle.load();

			if(handle.isVerbose) {
				System.out.println("[H2Handle.LoadingTask] Asset has been read successully!");
			}

			/* <state switch-section> */
			handle.transition(true, State.READY);
			/* </state switch-section> */


		} 
		catch (IOException e) {

			if(handle.isVerbose) {
				System.out.println("[H2Handle.LoadingTask] Failed to read asset!");
			}

			handle.error = new SiException(0, "FAILED_TO_LOAD: "+e.getMessage());

			/* <state switch-section> */
			handle.transition(true, State.FAILED_TO_LOAD);
			/* </state switch-section> */
		}

	}	

}
