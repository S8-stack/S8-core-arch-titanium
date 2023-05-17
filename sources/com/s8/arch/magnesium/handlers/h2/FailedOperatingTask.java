package com.s8.arch.magnesium.handlers.h2;

import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 * @param <M>
 * @param <O>
 */
public class FailedOperatingTask<M> extends H2Task<M> {
	
	@Override
	public MthProfile profile() { 
		return operator.profile(); 
	}
	
	
	@Override
	public String describe() { 
		return "H2 Operating (READY) of "+this; 
	}
	
	
 	private final H2Operator<M> operator;
	
	public FailedOperatingTask(H2Handle<M> handle, H2Operator<M> operator) {
		super(handle);
		this.operator = operator;
	}
	
	@Override
	public void run() {
		
		// has mutated
		operator.onReady(handle.model);
		
		
		/* <state switch-section> */
		handle.transition(true, null);
		/* <state switch-section> */
		
	}
}