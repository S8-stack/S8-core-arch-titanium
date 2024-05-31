package com.s8.core.arch.titanium.db;

/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public abstract class TiOperation<R> {

	
	
	public final TiDbHandler<R> handler;
	
	

	
	public TiOperation(TiDbHandler<R> handler) {
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
