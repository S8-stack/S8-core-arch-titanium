package com.s8.core.arch.titanium.db.requests;

public abstract class DeleteTiRequest<R> extends TiRequest<R> {
	
	
	public enum ResponseStatus {

		/**
		 *  Successfully written 
		 */
		SUCCESSFULLY_DELETED,
		
		/**
		 * IO Exception raised in the process
		 */
		IO_FAILED,

		/**
		 * key is already used and dry delete option was turned offs
		 */
		NO_RESOURCE;

	}

	public @Override Type getType() { return Type.DELETE; }

	

	public DeleteTiRequest(long t, String key) {
		super(t, key);
	}
	
	
	
	public abstract void onProcessed(ResponseStatus status);
	
	

	
}
