package com.s8.arch.magnesium.stores.m3.nodes;

/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public interface M3Node<T> {
	
	
	public enum Kind {
		FORK, BUCKET, LINK;
	}
	

	
	/**
	 * 
	 * @return
	 */
	public abstract Kind getKind();

}
