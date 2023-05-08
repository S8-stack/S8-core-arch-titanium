package com.s8.arch.magnesium.stores;

import com.s8.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public abstract class S8Flow {
	
	
	/**
	 * Use the field <code>repository</code> identity for:
	 * <ul>
	 * <li>define the target repository for commit</li>
	 * <li>define the origin repository for clone</li>
	 * </ul>
	 * 
	 */
	public String repository;
	
	
	/**
	 * Use the field <code>branch</code> identity for:
	 * <ul>
	 * <li>define the target branch for commit</li>
	 * <li>define the origin branch for clone</li>
	 * </ul>
	 * 
	 */
	public String branch;
	
	
	/**
	 * version
	 */
	public long version;
	
	
	/**
	 * objects
	 */
	public Object[] objects;

	
	/**
	 * 
	 * @param profile
	 * @param runnable
	 */
	public abstract void prior(MthProfile profile, S8Runnable runnable);
	
	
	
	/**
	 * 
	 * @param profile
	 * @param runnable
	 */
	public abstract void then(MthProfile profile, S8Runnable runnable);
	
	
	/**
	 * 
	 * @param pre
	 * @param post
	 */
	public abstract void commit(S8Runnable pre, S8Runnable post);
	
	
	/**
	 * 
	 * @param pre
	 * @param post
	 */
	public abstract void cloneHead(S8Runnable pre, S8Runnable post);
	
	
	/**
	 * 
	 * @param pre
	 * @param post
	 */
	public abstract void cloneVersion(S8Runnable pre, S8Runnable post);
	
	
	
}
