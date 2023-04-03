package com.s8.stack.arch.magnesium.stores.m1.modules;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import com.s8.stack.arch.magnesium.stores.m1.M1Handle;
import com.s8.stack.arch.magnesium.stores.m1.M1Store;



/**
 * 
 * @author pc
 *
 */
public class M1StoreModelsSaver<M> {


	/**
	 * 
	 * @author pc
	 *
	 */
	private class Crawler implements BiConsumer<String, M1Handle<M>> {

		private Queue<String> addresses;

		public Crawler(Queue<String> addresses) {
			super();
			this.addresses = addresses;
		}

		@Override
		public void accept(String address, M1Handle<M> handler) {
			addresses.add(address);
		}
	}


	/**
	 * 
	 */
	public final static long MAX_NB_LOOPS = 4294967296L;


	/**
	 * 
	 */
	private M1Store<M>.Facet store;


	private Lock lock;

	/**
	 * 
	 * @param store
	 * @param capacity
	 */
	public M1StoreModelsSaver(M1Store<M>.Facet store) {
		super();
		this.store = store;
		this.lock = new ReentrantLock();
	}




	/**
	 * Save on an active point in time. 
	 */
	public void save() {

		// protect
		lock.lock();

		Queue<String> toBeSaved = new LinkedList<String>();
		store.traverse(new Crawler(toBeSaved));

		long iLoop = 0, maxLoops = 2048 * toBeSaved.size();

		while(!toBeSaved.isEmpty() && iLoop++ < maxLoops) {
			String address = toBeSaved.poll();

			/* <unit-disposal-op> */
			M1Handle<M> handler = store.getModelHandler0(address);
			if(handler != null) {
				handler.requestSave();	
			}
		}
		if(store.isVerbose()) {
			System.out.println("M1 Store SAVER : "+iLoop);
		}

		// protect
		lock.unlock();
	}


	
}
