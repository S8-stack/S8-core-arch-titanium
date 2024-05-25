package com.s8.core.arch.magnesium.db.requests;

import com.s8.core.arch.magnesium.db.MgResourceStatus;
import com.s8.core.arch.silicon.async.MthProfile;

public abstract class AccessMgRequest<R> extends MgRequest<R> {
	
	
	public @Override Type getType() { return Type.ACCESS; }

	
	public final boolean isImmediateSyncRequired;
	
	

	public AccessMgRequest(long t, String key, boolean isImmediateSyncRequired) {
		super(t, key);
		this.isImmediateSyncRequired = isImmediateSyncRequired;
	}


	/**
	 * 
	 * @return
	 */
	public abstract MthProfile profile();


	/**
	 * 
	 * @return
	 */
	public abstract String describe();

	
	
	/**
	 * 
	 * @param resource
	 */
	public abstract boolean onResourceAccessed(MgResourceStatus status, R resource);

}
