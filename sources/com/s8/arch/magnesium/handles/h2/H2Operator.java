package com.s8.arch.magnesium.handles.h2;

import com.s8.arch.silicon.SiException;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public interface H2Operator<M> {


	/**
	 * @return profile of an M1Task running this operator
	 */
	public MthProfile profile();

	/**
	 * 
	 * @param asset the asset to perform operation on
	 * @return (has mutated asset) true if operator generate changes in asset passed
	 *         as argument worth a new SAVE operation.
	 */
	public abstract void onReady(M asset);

	/**
	 * 
	 * @return (has mutated asset) true if operator generate changes in asset passed
	 *         as argument worth a new SAVE operation.
	 */
	public abstract void onFailed(SiException exception);

}