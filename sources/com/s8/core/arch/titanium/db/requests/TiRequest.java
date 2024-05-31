package com.s8.core.arch.titanium.db.requests;

public abstract class TiRequest<R> {
	
	
	public enum Type {
		
		ACCESS,
		
		CREATE,
		
		DELETE;
	}
	
	
	/**
	 * timestamp
	 */
	public final long t;
	
	
	/**
	 * 
	 */
	public final String mgKey;

	
	public TiRequest(long t, String key) {
		super();
		this.t = t;
		this.mgKey = key;
	}
	
	public abstract Type getType();
}
