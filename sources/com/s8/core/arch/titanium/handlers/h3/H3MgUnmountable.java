package com.s8.core.arch.titanium.handlers.h3;

import com.s8.core.arch.titanium.callbacks.BooleanMgCallback;

public interface H3MgUnmountable {
	
	
	/**
	 * 
	 * @param cutOffTimestamp
	 * @param callback
	 */
	public void unmount(long cutOffTimestamp, BooleanMgCallback onUnmounted);
	
	
	
}
