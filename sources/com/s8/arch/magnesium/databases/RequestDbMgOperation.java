package com.s8.arch.magnesium.databases;

import com.s8.arch.fluor.S8User;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;

public abstract class RequestDbMgOperation<R> extends RequestH3MgOperation<R> {
	

	public final S8User initiator;
	
	public final long options;

	
	public RequestDbMgOperation(long timeStamp, S8User initiator, long options) {
		super(timeStamp);
		this.initiator = initiator;
		this.options = options;
	}

	
	/**
	 * 
	 * @return
	 */
	public S8User getInitiator() {
		return initiator;
	}
	

}
