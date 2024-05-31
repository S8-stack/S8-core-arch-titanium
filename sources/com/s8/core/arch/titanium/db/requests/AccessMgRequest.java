package com.s8.core.arch.titanium.db.requests;

import java.nio.file.Path;

import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.MgResourceStatus;

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
	 * @param resourceFolderPath the folder dedicated to this resource on disk
	 * @param status
	 * @param resource
	 * @return
	 */
	public abstract boolean onResourceAccessed(Path resourceFolderPath, MgResourceStatus status, R resource);

}
