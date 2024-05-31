package com.s8.core.arch.titanium.db;

import com.s8.core.arch.titanium.db.requests.TiRequest;

/**
 * 
 * CREATE
 * ACCESS
 * DELETE
 * 
 * @param <R>
 */
abstract class RequestOp<R> extends TiOperation<R> {



	public RequestOp(TiDbHandler<R> handler) {
		super(handler);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public abstract TiRequest<R> getRequest();
	
	
	@Override
	public long getTimestamp() {
		return getRequest().t;
	}
}
