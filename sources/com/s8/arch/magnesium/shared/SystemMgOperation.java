package com.s8.arch.magnesium.shared;

public abstract class SystemMgOperation<R> extends MgOperation<R> {

	
	public final MgSharedResourceHandler<R> handler;
	

	public SystemMgOperation(MgSharedResourceHandler<R> handler) {
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
