package com.s8.arch.magnesium.handles.h2;

import com.s8.arch.silicon.async.MthProfile;



public class ReadyOperatingTask<M> extends H2Task<M> {

	@Override
	public MthProfile profile() { 
		return operator.profile(); 
	}


	@Override
	public String describe() { 
		return "H2 Operating (READY) of "+this; 
	}


	private final H2Operator<M> operator;

	public ReadyOperatingTask(H2Handle<M> handle, H2Operator<M> operator) {
		super(handle);
		this.operator = operator;
	}

	@Override
	public void run() {


		// run main methdo
		operator.onReady(handle.model);

		/*
		 * erase bytecount NOTE: It is the responsibility of the prototype getBytecount
		 * method to efficiently cache byte count computations
		 */
		handle.updateBytecount();


		/* <state switch-section> */
		handle.transition(true, null);
		/* <state switch-section> */

	}
}