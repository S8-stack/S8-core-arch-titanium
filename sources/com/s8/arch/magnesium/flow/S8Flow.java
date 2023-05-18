package com.s8.arch.magnesium.flow;

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
	public String repositoryAddress;
	
	
	/**
	 * Use the field <code>branch</code> identity for:
	 * <ul>
	 * <li>define the target branch for commit</li>
	 * <li>define the origin branch for clone</li>
	 * </ul>
	 * 
	 */
	public String branchId;
	
	
	/**
	 * version
	 */
	public long version;
	
	
	/**
	 * objects of the graph exposure (Neodymium)
	 */
	public Object[] objects;

	
	/**
	 * 
	 * @param profile
	 * @param runnable
	 */
	public abstract S8Flow prior(MthProfile profile, S8Runnable runnable);
	
	
	
	/**
	 * 
	 * @param profile
	 * @param runnable
	 */
	public abstract S8Flow then(MthProfile profile, S8Runnable runnable);
	
	
	/**
	 * 
	 * @param pre
	 * @param post
	 */
	public abstract S8Flow commit(S8Runnable pre, S8Runnable post);
	
	
	/**
	 * 
	 * @param pre
	 * @param post
	 */
	public abstract S8Flow cloneHead(S8Runnable pre, S8Runnable post);
	
	
	/**
	 * 
	 * @param pre
	 * @param post
	 */
	public abstract S8Flow cloneVersion(S8Runnable pre, S8Runnable post);
	
	
	
}
