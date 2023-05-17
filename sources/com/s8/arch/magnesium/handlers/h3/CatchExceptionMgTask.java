package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncTask;

public abstract class CatchExceptionMgTask implements AsyncTask {

	public final Exception exception;
	
	public CatchExceptionMgTask(Exception exception) {
		super();
		this.exception = exception;
	}
	

	
	
	
	public abstract void catchException(Exception exception);

	@Override
	public void run() {
		
		catchException(exception);
		
		
	}
	
}
