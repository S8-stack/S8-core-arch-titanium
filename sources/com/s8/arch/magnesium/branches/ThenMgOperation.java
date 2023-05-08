package com.s8.arch.magnesium.branches;

import com.s8.arch.magnesium.stores.MgFlow;
import com.s8.arch.magnesium.stores.S8Runnable;
import com.s8.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class ThenMgOperation extends MgOperation {

	
	
	/**
	 * profile
	 */
	public final MthProfile profile;
	
	
	/**
	 * runnable
	 */
	public final S8Runnable runnable;

	
	/**
	 * 
	 * @param runnable
	 */
	public ThenMgOperation(MgFlow flow, MthProfile profile, S8Runnable runnable) {
		super(flow);
		this.profile = profile;
		this.runnable = runnable;
	}
	
}
