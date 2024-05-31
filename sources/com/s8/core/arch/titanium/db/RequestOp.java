package com.s8.core.arch.titanium.db;

import com.s8.core.arch.titanium.db.requests.MgRequest;

/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
abstract class RequestOp<R> extends MgOperation<R> {



	public RequestOp(MgDbHandler<R> handler) {
		super(handler);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public abstract MgRequest<R> getRequest();
	
	
	@Override
	public long getTimestamp() {
		return getRequest().t;
	}
}
