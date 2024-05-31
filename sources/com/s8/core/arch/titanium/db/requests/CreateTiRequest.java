package com.s8.core.arch.titanium.db.requests;

import java.nio.file.Path;

public abstract class CreateTiRequest<R> extends TiRequest<R> {

	public @Override Type getType() { return Type.CREATE; }

	
	
	public final R resource;
	
	public final boolean isOverridingEnabled;
	
	public final boolean isResourceSavedToDisk;
	

	public CreateTiRequest(long t, String key, R resource, 
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
