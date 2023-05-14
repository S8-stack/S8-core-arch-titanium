package com.s8.arch.magnesium.shared;

import com.s8.arch.magnesium.callbacks.VoidMgCallback;

public interface MgUnmountable {
	
	
	/**
	 * 
	 * @param cutOffTimestamp
	 * @param callback
	 */
	public void unmount(long cutOffTimestamp, VoidMgCallback callback);
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isDetachable();
	
	
}
