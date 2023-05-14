package com.s8.arch.magnesium.shared;

import com.s8.arch.magnesium.callbacks.VoidMgCallback;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;

public class UnmountOp<R> extends SystemMgOperation<R> {

	public final long cutOffTimestamp;

	public final VoidMgCallback callback;


	public UnmountOp(MgSharedResourceHandler<R> handler, long cutOffTimestamp, VoidMgCallback callback) {
		super(handler);
		this.cutOffTimestamp = cutOffTimestamp;
		this.callback = callback;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public UnmountMgTask<R> createConsumeResourceTask(R resource) {
		return new UnmountMgTask<R>(handler, resource, cutOffTimestamp, callback);
	}

	@Override
	public AsyncTask createCatchExceptionTask(Exception exception) {
		return new AsyncTask() {


			@Override
			public void run() {

				exception.printStackTrace();
				
				/**
				 * 
				 */
				handler.roll(true);
			}



			@Override
			public String describe() {
				return "handling failed resource access for handler: "+handler.getName();
			}



			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}
		};
	}

}
