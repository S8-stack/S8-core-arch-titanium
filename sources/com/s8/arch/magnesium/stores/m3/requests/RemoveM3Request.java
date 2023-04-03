package com.s8.arch.magnesium.stores.m3.requests;

import java.io.IOException;

import com.s8.arch.magnesium.stores.m3.HashcodeModule;
import com.s8.arch.magnesium.stores.m3.M3Store;
import com.s8.arch.magnesium.stores.m3.nodes.LinkM3Node;

public class RemoveM3Request<T> extends Query<T> implements M3Request<T> {

	

	private boolean isTransactional;
	
	private boolean isCompleted;

	private T value;
	
	public RemoveM3Request(M3Store<T> store, String key, boolean isTransactional) {
		super(store, Action.REMOVE, key, HashcodeModule.compute(key), store.getHead());
		this.isTransactional = isTransactional;
		
		isCompleted = false;
	}


	@Override
	public void serve() throws IOException {
		if(!isCompleted) {
			
			store.dim();
			LinkM3Node<T> link = lookUp();

			// save last fork
			getLastFork().save(store);
			store.rollOver();
			
			value = link!=null ? link.value : null;
			
			store.rollOver();
			
			if(isTransactional) {
				store.persist();	
			}
			
			isCompleted = true;
		}
	}
	
	
	public T getValue() {
		return value;
	}
	
}
