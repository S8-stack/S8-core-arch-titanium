package com.s8.arch.magnesium.handlers.h3;

public abstract class SystemH3MgOperation<R> extends H3MgOperation<R> {

	
	public final H3MgHandler<R> handler;
	

	public SystemH3MgOperation(H3MgHandler<R> handler) {
		super();
		this.handler = handler;
	}	
	
	
	@Override
	public long getTimestamp() {
		return 0;
	}
	
	@Override
	public boolean isUserInduced() {
		return false;
	}
}
