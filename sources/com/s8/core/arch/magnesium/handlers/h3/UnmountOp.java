package com.s8.core.arch.magnesium.handlers.h3;

import com.s8.core.arch.magnesium.callbacks.BooleanMgCallback;

public class UnmountOp<R> extends SystemH3MgOperation<R> {

	public final long cutOffTimestamp;

	public final BooleanMgCallback callback;


	public UnmountOp(H3MgHandler<R> handler, long cutOffTimestamp, BooleanMgCallback onUnmounted) {
		super(handler);
		this.cutOffTimestamp = cutOffTimestamp;
		this.callback = onUnmounted;
	}


	@Override
	public UnmountMgAsyncTask<R> createAsyncTask() {
		return new UnmountMgAsyncTask<R>(handler, cutOffTimestamp, callback);
	}


}
