package com.s8.core.arch.titanium.db.requests;

import java.nio.file.Path;

import com.s8.core.arch.silicon.async.MthProfile;

public abstract class AccessTiRequest<R> extends TiRequest<R> {
	
	
	public enum ResponseStatus {
		
		
		/**
		 * Successfully accessed
		 */
		SUCCESSFULLY_ACCESSED,
		
		/**
		 * The resource has been deleted unsafe to use
		 */
		DELETED,
		
		
		/**
		 * The resource has never been initialized (nothing on Disk)
		 * No unsaved modification (nothing on Disk, no resource set to the handler
		 */
		NO_RESOURCE_FOR_KEY,

		
		/**
		 * I/O exception was raised when attempting to load from disk
		 */
		FAILED_TO_LOAD;

		
	}
	
	
	public @Override Type getType() { return Type.ACCESS; }

	
	public final boolean isImmediateSyncRequired;
	
	

	public AccessTiRequest(long t, String key, boolean isImmediateSyncRequired) {
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
	public abstract boolean onProcessed(Path resourceFolderPath, ResponseStatus status, R resource);

}
