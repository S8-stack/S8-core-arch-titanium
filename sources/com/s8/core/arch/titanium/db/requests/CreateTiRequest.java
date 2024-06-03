package com.s8.core.arch.titanium.db.requests;

import java.nio.file.Path;

public abstract class CreateTiRequest<R> extends TiRequest<R> {


	public enum ReturnedStatus {

		/**
		 *  Successfully written 
		 */
		SUCCESSFULLY_CREATED,
		
		/**
		 * IO Exception raised in the process
		 */
		IO_FAILED,

		/**
		 * key is already used and overwite option was turned off when attempting to create
		 */
		CONFLICT_ON_KEY;


	}


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



	/**
	 * 
	 * @param status
	 */
	public abstract void onProcessed(ReturnedStatus status);



}
