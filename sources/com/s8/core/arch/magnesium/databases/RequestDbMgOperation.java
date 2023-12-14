package com.s8.core.arch.magnesium.databases;

import com.s8.api.flow.S8User;
import com.s8.core.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.core.arch.silicon.SiliconChainCallback;

public abstract class RequestDbMgOperation<R> extends RequestH3MgOperation<R> {
	

	public final S8User initiator;
	
	/**
	 * callback
	 */
	public final SiliconChainCallback callback;
	

	
	public RequestDbMgOperation(long timeStamp, S8User initiator, SiliconChainCallback callback) {
		super(timeStamp);
		this.initiator = initiator;
		this.callback = callback;
	}

	
	/**
	 * 
	 * @return
	 */
	public S8User getInitiator() {
		return initiator;
	}
	

}
