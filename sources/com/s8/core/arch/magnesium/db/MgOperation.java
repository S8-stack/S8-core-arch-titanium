package com.s8.core.arch.magnesium.db;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public abstract class MgOperation<R> {

	
	
	public final MgHandler<R> handler;
	
	

	
	public MgOperation(MgHandler<R> handler) {
		super();
		this.handler = handler;
	}

	
	/**
	 * 
	 * @param resource
	 */
	public abstract void perform();


	/**
	 * 
	 * user induced
	 * if timestamp >= 0: interpreted as true time stamp
	 * 
	 * System induced
	 * if timestamp < 0 : no time stamp
	 */
	public abstract long getTimestamp();

	
	
	protected void terminate() {
		
		/* roll from hot state */
		handler.roll(true);
	}


}
