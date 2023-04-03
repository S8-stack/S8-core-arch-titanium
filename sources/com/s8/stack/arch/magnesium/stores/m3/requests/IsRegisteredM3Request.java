package com.s8.stack.arch.magnesium.stores.m3.requests;

import java.io.IOException;

import com.s8.stack.arch.magnesium.stores.m3.HashcodeModule;
import com.s8.stack.arch.magnesium.stores.m3.M3Store;
import com.s8.stack.arch.magnesium.stores.m3.nodes.LinkM3Node;

public class IsRegisteredM3Request<T> extends Query<T> implements M3Request<T>{


	private boolean isRegistered;

	private boolean isCompleted;

	public IsRegisteredM3Request(M3Store<T> store, String key) {
		super(store, Action.RETRIEVE, key, HashcodeModule.compute(key), store.getHead());
	}


	@Override
	public void serve() throws IOException {
		if(!isCompleted) {
			store.dim();
			LinkM3Node<T> link = lookUp();
			isRegistered = link!=null;
			
			store.rollOver();
			
			isCompleted = true;
		}
	}

	
	public boolean getValue() throws IOException {
		serve();
		return isRegistered;
	}
}
