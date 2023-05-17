package com.s8.arch.magnesium.handlers.h1;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.s8.arch.silicon.SiException;
import com.s8.arch.silicon.SiliconEngine;

/**
 * 
 * @author pierreconvert
 *
 */
public abstract class H1Handler<A> {


	public enum State {

		/** (Mapping to zero) 
		 */
		NOT_COMPUTED,

		/** (Mapping to resource)  
		 * Give access to resource  */
		COMPUTED, 

		/** (Mapping to fallback resource)
		 * Give to access to fallback resource */
		FAILED_TO_COMPUTE,

		/** (Mapping to parking)
		 * Store request for later serving it */
		COMPUTING,

		/**
		 * (final state)
		 */
		SHUT_DOWN;

	}



	/**
	 * 
	 * @author pierreconvert
	 *
	 * @param <T>
	 */
	public interface Callback<A> {


		/**
		 * (let-through state)
		 * @param app so we can launch next step of process
		 * @param retrievalInterface
		 */
		public void onSuccessful(A asset);


		/**
		 * 
		 * @param app
		 * @param error
		 */
		public void onFailed(SiException error);

	}


	/**
	 * (retention state)
	 * @param app
	 * @param drive
	 */
	public abstract void compute();







	private final Lock lock;

	private final Queue<Callback<A>> callbacks;



	/**
	 * 
	 */
	private volatile State state;

	private A object;

	private SiException error;


	/**
	 * 
	 * @param app
	 */
	public H1Handler() {
		super();

		// lock
		this.lock = new ReentrantLock();

		// max log
		this.callbacks = new ArrayBlockingQueue<>(1024);


		//
		state = State.NOT_COMPUTED;

	}


	public abstract SiliconEngine getAppEngine();



	public void access(Callback<A> callback) {
		switch(state) {

		case NOT_COMPUTED:
			// append callback
			callbacks.add(callback);

			/* <secure-section> */
			lock.lock();

			// state race check
			if(state==State.NOT_COMPUTED) {

				// switch state
				state = State.COMPUTING;

				// trigger init routine
				compute();
			}

			lock.unlock();
			/* <secure-section> */
			break;

		case COMPUTING:
			// append callback without anything else to do
			callbacks.add(callback);
			break;


		case COMPUTED:
			callback.onSuccessful(object);
			break;

		case FAILED_TO_COMPUTE:
			callback.onFailed(error);
			break;	

		case SHUT_DOWN:
			// append callback without anything else to do
			callbacks.add(callback);
			break;
		}
	}


	/**
	 * 
	 * @param obj
	 */
	public void switchToSuccessful(A obj) {

		lock.lock();
		object = obj;

		// set spin to true, so we stop accumulating new callbacks
		state = State.COMPUTED;

		// un-park all
		Callback<A> callback;
		while((callback = callbacks.poll()) != null) {
			callback.onSuccessful(object);
		}


		lock.unlock();
	}


	/**
	 * 
	 * @param error
	 */
	public void switchToError(SiException error) {
		lock.lock();
		this.error = error;

		// set spin to true, so we stop accumulating new callbacks
		state = State.FAILED_TO_COMPUTE;

		// un-park all
		Callback<A> callback;
		while((callback = callbacks.poll()) != null) {
			callback.onFailed(error);
		}
		lock.unlock();
	}


	/**
	 * 
	 */
	public void switchToNotInitiated() {
		lock.lock();
		state = State.NOT_COMPUTED;
		lock.unlock();
	}


	/**
	 * 
	 */
	public void switchToShutDown() {
		lock.lock();
		state = State.SHUT_DOWN;		
		lock.unlock();
	}
	
	
	/**
	 * 
	 * @param consumer
	 */
	public void reroute(Consumer<Callback<A>> consumer) {
		Callback<A> callback;
		while((callback = callbacks.poll()) != null) {
			consumer.accept(callback);
		}
	}

}
