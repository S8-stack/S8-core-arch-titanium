package com.s8.stack.arch.magnesium.stores.m3.requests;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import com.s8.stack.arch.magnesium.stores.m3.M3Store;
import com.s8.stack.arch.magnesium.stores.m3.nodes.ForkM3Node;

public class BrowseM3Request<T> implements M3Request<T> {

	
	
	public final M3Store<T> store;
	
	private final BiConsumer<String, T> consumer;

	private Queue<ForkM3Node<T>> queue;
	
	
	/**
	 * 
	 * @param store
	 * @param consumer
	 */
	public BrowseM3Request(M3Store<T> store, BiConsumer<String, T> consumer) {
		super();
		this.store = store;
		this.consumer = consumer;
		
		queue = new LinkedList<ForkM3Node<T>>();
		queue.add(store.getHead());
	}


	@Override
	public void serve() throws IOException {
		ForkM3Node<T> node;
		int c = 0;
		store.rollOver();
		while((node = queue.poll()) !=null) {
			node.traverse(store, consumer, queue);
			if(c++ > 7) {
				store.rollOver();
				c = 0;
			}
		}
	}
	
}
