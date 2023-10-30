package com.s8.arch.magnesium.handlers.h2;

import java.io.IOException;

import com.s8.core.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class ShuttingDownTask<M> extends H2Task<M> {


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
	public ShuttingDownTask(H2Handle<M> handle) {
		super(handle);
	}


	@Override
	public void run() {

		if(handle.isVerbose) {
			System.out.println("Starting SHUTTING-DOWN sequence");
		}

		try {
			handle.save();
		} 
		catch (IOException e) {
			if(handle.isVerbose) {
				e.printStackTrace();	
			}
		}


		if(handle.isVerbose) {
			H2Handle.DEBUG_nSaved++;
			System.out.println("[SHUTTING-DOWN sequence]: resource successfully save to disk ("+
					H2Handle.DEBUG_nSaved+")");
		}

		handle.terminate();
	}

}
