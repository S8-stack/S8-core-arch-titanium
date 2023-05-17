package com.s8.arch.magnesium.core.handler;

import com.s8.arch.magnesium.core.callbacks.BooleanMgCallback;

public interface MgUnmountable {
	
	
	/**
	 * 
	 * @param cutOffTimestamp
	 * @param callback
	 */
	public void unmount(long cutOffTimestamp, BooleanMgCallback onUnmounted);
	
	
	
}
