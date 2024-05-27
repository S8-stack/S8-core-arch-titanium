package com.s8.core.arch.magnesium.db.requests;

public abstract class MgRequest<R> {
	
	
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

	
	public MgRequest(long t, String key) {
		super();
		this.t = t;
		this.mgKey = key;
	}
	
	public abstract Type getType();
}
