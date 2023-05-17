package com.s8.arch.magnesium.core.handler;

public abstract class SystemMgOperation<R> extends MgOperation<R> {

	
	public final MgHandler<R> handler;
	

	public SystemMgOperation(MgHandler<R> handler) {
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
