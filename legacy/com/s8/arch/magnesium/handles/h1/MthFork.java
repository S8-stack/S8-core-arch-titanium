package com.s8.arch.magnesium.handles.h1;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.s8.arch.silicon.SiliconEngine;

/**
 * 
 * @author pierreconvert
 *
 */
public abstract class MthFork<A, E> {


	public enum State {

		/** (Mapping to zero) 
		 */
		NOT_INITIATED,
		
		/** (Mapping to resource)  
		 * Give access to resource  */
		SUCCESSFUL, 
		
		/** (Mapping to fallback resource)
		 * Give to access to fallback resource */
		FAILED,

		/** (Mapping to parking)
		 * Store request for later serving it */
		WAITING, 

		/** (Mapping to reroute)
		 * reroute request */
		FORWARDED;

	}



	/**
	 * 
	 * @author pierreconvert
	 *
	 * @param <T>
	 */
	public interface Callback<A, E> {
		

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
		public void onFailed(E error);
		

		/**
		 * 
		 * @param app
		 */
		public void onForwarded();

	}
	

	/**
	 * (retention state)
	 * @param app
	 * @param drive
	 */
	public abstract void compute(Actuator<A, E> actuator);

	


	/**
	 * 
	 * @author pierreconvert
	 *
	 * @param <T>
	 */
	public interface Actuator<A, E> {

		/**
		 * 
		 * @param asset
		 */
		public void serve(A asset);
		
		/**
		 * 
		 * @param asset
		 */
		public void fail(E error);
		
		
		public void reset();


		/**
		 * 
		 */
		public void forward();


	}



	public interface Updater<A, E> {


		/**
		 * 
		 * @param actuator
		 * @param state
		 * @return
		 */
		public void update(State state, Actuator<A, E> actuator);

	}



	private final Lock lock;

	private final Queue<Callback<A, E>> callbacks;

	private final Actuator<A, E> actuator = new Actuator<>() {


		@Override
		public void serve(A obj) {
			object = obj;

			// set spin to true, so we stop accumulating new callbacks
			state = State.SUCCESSFUL;

			// un-park all
			Callback<A, E> callback;
			while((callback = callbacks.poll()) != null) {
				callback.onSuccessful(object);
			}
		}
		
		@Override
		public void fail(E error) {
			fallback = error;

			// set spin to true, so we stop accumulating new callbacks
			state = State.FAILED;

			// un-park all
			Callback<A, E> callback;
			while((callback = callbacks.poll()) != null) {
				callback.onFailed(fallback);
			}
		}

		
		@Override
		public void reset() {
			state = State.NOT_INITIATED;
		}
		

		@Override
		public void forward() {
			state = State.FORWARDED;

			Callback<A, E> callback;
			while((callback = callbacks.poll()) != null) {
				callback.onForwarded();
			}
		}
	};


	/**
	 * 
	 */
	private volatile State state;

	private A object;
	
	private E fallback;


	/**
	 * 
	 * @param app
	 */
	public MthFork() {
		super();

		// lock
		this.lock = new ReentrantLock();

		// max log
		this.callbacks = new ArrayBlockingQueue<>(1024);


		//
		state = State.NOT_INITIATED;

	}


	public abstract SiliconEngine getApp();



	public void serve(Callback<A, E> callback) {
		switch(state) {

		case NOT_INITIATED:
			// append callback
			callbacks.add(callback);

			/* <secure-section> */
			lock.lock();
			
			// state race check
			if(state==State.NOT_INITIATED) {
				
				// switch state
				state = State.WAITING;
				
				// trigger init routine
				compute(actuator);
			}

			lock.unlock();
			/* <secure-section> */
			break;
			
		case WAITING:
			// append callback without anything else to do
			callbacks.add(callback);
			break;

		
		case SUCCESSFUL:
			callback.onSuccessful(object);
			break;
			
		case FAILED:
			callback.onFailed(fallback);
			break;	
			
		case FORWARDED:
			callback.onForwarded();
			break;
		}
	}



	/**
	 * 
	 * @param updater
	 */
	public void update(Updater<A, E> updater) {
		/* <secure-section> */
		lock.lock();

		updater.update(state, actuator);

		lock.unlock();
		/* <secure-section> */
	}
	


}
