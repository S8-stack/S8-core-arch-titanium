package com.s8.core.arch.magnesium.db.requests;

import java.nio.file.Path;

public abstract class CreateMgRequest<R> extends MgRequest<R> {

	public @Override Type getType() { return Type.CREATE; }

	
	
	public final R resource;
	
	public final boolean isOverridingEnabled;
	
	public final boolean isResourceSavedToDisk;
	

	public CreateMgRequest(long t, String key, R resource, 
			boolean isResourceSavedToDisk,
			boolean isOverridingEnabled) {
		super(t, key);
		this.resource = resource;
		this.isOverridingEnabled = isOverridingEnabled;
		this.isResourceSavedToDisk = isResourceSavedToDisk;
	}
	
	
	
	/**
	 * 
	 * @param resourceFolderPath the folder dedicated to this resource
	 */
	public abstract void onPathGenerated(Path resourceFolderPath);
	
	
	
	public abstract void onEntryCreated(boolean isSucessful);
	
	

}
