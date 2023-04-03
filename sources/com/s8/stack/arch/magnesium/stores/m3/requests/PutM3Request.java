package com.s8.stack.arch.magnesium.stores.m3.requests;

import java.io.IOException;

import com.s8.stack.arch.magnesium.stores.m3.HashcodeModule;
import com.s8.stack.arch.magnesium.stores.m3.M3Store;

public class PutM3Request<T> extends Query<T> implements M3Request<T> {

	
	
	private boolean isCompleted;
	
	private boolean isTransactional;
	
	private T value;
	
	//private List<ForkM3Node<T>> DEBUG_steps = new ArrayList<ForkM3Node<T>>();

	/**
	 * 
	 * @param store
	 * @param action
	 * @param key
	 * @param hashcode
	 * @param lastFork
	 */
	public PutM3Request(M3Store<T> store, String key, T value, boolean isTransactional) {
		super(store, Action.APPEND, key, HashcodeModule.compute(key), store.getHead());
		this.value = value;
		this.isTransactional = isTransactional;
		
		isCompleted = false;
	}

	


	@Override
	public void serve() throws IOException {
		if(!isCompleted) {
			store.dim();
			lookUp().value = value;
			
			// save last fork
			getLastFork().notifyChange();

			
			store.rollOver();
			
			if(isTransactional) {
				store.persist();	
			}
			
			isCompleted = true;
		}
	}

}
