package com.s8.core.arch.magnesium.handlers.h2;

import java.io.IOException;

import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class SavingTask<M> extends H2Task<M> {


	public SavingTask(H2Handle<M> handle) {
		super(handle);
	}


	@Override
	public String describe() {
		return "SAVING : "+handle.describe();
	}


	@Override
	public MthProfile profile() {
		return MthProfile.IO_DATA_LAKE;
	}




	@Override
	public void run() {
		try {
			handle.save();

			handle.DEBUG_log += "|saved";
			H2Handle.DEBUG_nSaved++;

		} 
		catch (IOException e) {

			if(handle.isVerbose) {
				e.printStackTrace();	
			}
		}

		handle.transition(true, null);
	}

}
