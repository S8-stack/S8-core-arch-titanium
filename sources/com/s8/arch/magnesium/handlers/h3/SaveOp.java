package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncSiTask;


/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public class SaveOp<R> extends SystemH3MgOperation<R> {

	/**
	 * 
	 * @param handler
	 */
	public SaveOp(H3MgHandler<R> handler) {
		super(handler);
	}
	

	@Override
	public AsyncSiTask createAsyncTask() {
		return new SaveMgAsyncTask<R>(handler);
	}


}
