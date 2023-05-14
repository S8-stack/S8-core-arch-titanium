package com.s8.arch.magnesium.shared;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;

public interface MgUnmountable {
	
	
	/**
	 * 
	 * @param cutOffTimestamp
	 * @param callback
	 */
	public void unmount(long cutOffTimestamp, BooleanMgCallback onUnmounted);
	
	
	
}
