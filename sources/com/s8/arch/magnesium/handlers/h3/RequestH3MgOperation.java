package com.s8.arch.magnesium.handlers.h3;

/**
 * 
 * User-induced operation (start somehow by user action), different from system (garbage collecting, atomatic cache empptying) 
 * system induced operations.
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public abstract class RequestH3MgOperation<R> extends H3MgOperation<R> {


	/**
	 * 
	 */
	public final long timeStamp;
	

	public RequestH3MgOperation(long timeStamp) {
		super();
		this.timeStamp = timeStamp;
	}


	@Override
	public long getTimestamp() {
		return timeStamp;
	}

	
	@Override
	public boolean isUserInduced() {
		return true;
	}
	
	
	@Override
	public abstract ConsumeResourceMgTask<R> createConsumeResourceTask(R resource);

	

	@Override
	public abstract CatchExceptionMgTask createCatchExceptionTask(Exception exception);
	
	

}
