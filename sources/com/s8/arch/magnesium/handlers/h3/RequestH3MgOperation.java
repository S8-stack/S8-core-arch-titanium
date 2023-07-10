package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.fluor.S8User;

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
	
	public final S8User initiator;
	

	public RequestH3MgOperation(long timeStamp, S8User initiator) {
		super();
		this.timeStamp = timeStamp;
		this.initiator = initiator;
	}


	@Override
	public long getTimestamp() {
		return timeStamp;
	}

	
	/**
	 * 
	 * @return
	 */
	public S8User getInitiator() {
		return initiator;
	}
	
	
	@Override
	public boolean isUserInduced() {
		return true;
	}
	


}
