package com.s8.core.arch.magnesium.handlers.h2;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.s8.core.arch.silicon.SiException;
import com.s8.core.arch.silicon.SiliconEngine;



/**
 * <h1>H2Handle</h1>
 * <h2>General design purposes</h2>
 * <p>
 * H2 handler is design for low throughput, high consistency, namely;
 * </p>
 * <ul>
 * <li><b>Low throughput</b>: this handle is supposed to be used in case where
 * is using COPIES of the underlying asset. The replication in each user
 * workspace allow for tremendous badnwidth reduction on the handle. Simply
 * said: user is accessing the asser once, copy it, and use its own copy for
 * minutes, eventually ending its session with a commit on the asset: so 2
 * accesses in 12 min. We don't expect any particuliar handled asset to be a
 * bottleneck.</li>
 * <li><b>High consistency</b>: access are for READ and WRITE operations. At
 * first glance, we might think that READ would leave the asset unchanged, but
 * this point can in fact not be guaranteed. The main reason is that
 * <code>S8Graph</code> copying MIGHT require some re-mapping that will result
 * in minor changes in the shared asset. WRITE accesses are of course supposed
 * to change things but came with additional constraints that ordering actually
 * matters.</li>
 * <ul>
 * <p>
 * These two points imply that we MUST NOT allow concurrent accesses to the
 * underlying asset. Accesses must be ordered and sequential
 * </p>
 * <h2>Shutting-down procedure</h2>
 * <p>MUSt go through the following steps:</p>
 * <ul>
 * <li>If not saved, perform saving operation and MUST wait for this SAVE operation to complete and succeed</li>
 * <li>Switch to SHUT_DOWN so that operator are stored in queue</li>
 * 
 * 
 * 
 * @author pierreconvert
 *
 */
public abstract class H2Handle<M> {


	public static volatile int DEBUG_nSaved;
	
	public static volatile int DEBUG_nMods;
	
	

	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	public enum State {

		/** (Mapping to zero) 
		 */
		NOT_INITIATED,

		/** 
		 * (Mapping to resource)  
		 * Give access to resource  */
		READY,

		/** 
		 * (Mapping to fallback resource)
		 * Give to access to fallback resource 
		 */
		FAILED_TO_LOAD,

		/**
		 * (final state)
		 */
		SHUT_DOWN;

	}



	public static class Props {
		public int buffering = 1024;
		public int capacity = 1024;
		public boolean isVerbose = true;
	}



	public String DEBUG_log;

	final Lock lock;

	final Queue<H2Operator<M>> queue;


	/**
	 * 
	 */
	volatile State state;

	volatile boolean isRunning;


	/**
	 * 
	 */
	M model;


	/**
	 * 
	 */
	SiException error;



	volatile boolean isSavingRequired;

	volatile boolean isShuttingDownRequired;
	volatile C0Callback terminationCallback;
	
	public final int buffering;
	public final int capacity;
	public final boolean isVerbose;


	//H2Prototype<M> prototype;


	long bytecount = 0;
	
	

	/**
	 * 
	 * @param app
	 */
	public H2Handle(Props props) {
		super();

		// setup
		this.buffering = props.buffering;
		this.capacity = props.capacity;
		this.isVerbose = props.isVerbose;

		// lock
		this.lock = new ReentrantLock();

		/*
		 * 
		 * The ConcurrentLinkedQueue is the only non-blocking queue of this guide.
		 * Consequently, it provides a “wait-free” algorithm where add and poll are
		 * guaranteed to be thread-safe and return immediately. Instead of locks, this
		 * queue uses CAS (Compare-And-Swap).
		 * 
		 * Internally, it's based on an algorithm from Simple, Fast, and Practical
		 * Non-Blocking and Blocking Concurrent Queue Algorithms by Maged M. Michael and
		 * Michael L. Scott.
		 * 
		 * It's a perfect candidate for modern reactive systems, where using blocking
		 * data structures is often forbidden.
		 * 
		 * On the other hand, if our consumer ends up waiting in a loop, we should
		 * probably choose a blocking queue as a better alternative.
		 */
		this.queue = new ConcurrentLinkedQueue<>();


		// state
		state = State.NOT_INITIATED;
		isRunning = false;


		isSavingRequired = false;
		isShuttingDownRequired = false;

	}






	/**
	 * 
	 * @return
	 */
	public abstract H2ModelPrototype<M> getPrototype();


	/**
	 * 
	 * @return
	 */
	public abstract SiliconEngine getAppEngine();



	/**
	 * 
	 */
	public void operate(H2Operator<M> operator) {

		switch(state) {

		case NOT_INITIATED:
		case READY:
		case FAILED_TO_LOAD:
			
			// first: queue operator
			queue.add(operator);

			// transition
			transition(false, null);

			break;
			
		case SHUT_DOWN:
			reroute(operator);
			break;
		}
		

	
	}



	public abstract String describe();


	/**
	 * 
	 * @param model
	 */
	public void initializeReady(M model) {
		lock.lock();
		this.model = model;
		state = State.READY;
		lock.unlock();
	}
	
	public void initializeFailed(SiException error) {
		lock.lock();
		this.error = error;
		state = State.FAILED_TO_LOAD;
		lock.unlock();
	}



	/**
	 * 
	 * @param isReentry
	 * @param overridingState
	 */
	public void transition(boolean isReentry, State overridingState) {

		// acquire lock
		lock.lock();

		// check whether transition is possible
		if(isReentry || !isRunning) {

			// override if necessary
			if(overridingState != null) {
				state = overridingState;
			}

			// perform subsequent state change
			//lock.lock();

			H2Operator<M> operator;

			switch(state) {

			case NOT_INITIATED:
				if(isShuttingDownRequired){
					isRunning = true;
					isShuttingDownRequired = false;
					lock.unlock();
					getAppEngine().pushAsyncTask(new DryShuttingDownTask<>(this));
				}
				else {
					isRunning = true;
					lock.unlock();		
					// trigger init routine
					getAppEngine().pushAsyncTask(new LoadingTask<>(this));
				}			

				break;

				/** to do */
			case READY:
				if((operator = queue.poll()) != null) {
					isRunning = true;
					lock.unlock();
					getAppEngine().pushAsyncTask(new ReadyOperatingTask<>(this, operator));
				}
				else if(isSavingRequired){ // in case saving is required and no operation left
					isRunning = true;
					isSavingRequired = false;
					lock.unlock();
					DEBUG_nSaved++;
					getAppEngine().pushAsyncTask(new SavingTask<>(this));	

				}
				else if(isShuttingDownRequired){
					isRunning = true;
					isShuttingDownRequired = false;
					lock.unlock();
					getAppEngine().pushAsyncTask(new ShuttingDownTask<>(this));	

				}
				else {
					/* STOP RUNNING AT THIS POINT */
					isRunning = false;
					lock.unlock();
				}
				break;

				/** to do */
			case FAILED_TO_LOAD:
				if((operator = queue.poll()) != null) {
					isRunning = true;
					lock.unlock();
					getAppEngine().pushAsyncTask(new FailedOperatingTask<>(this, operator));
				}
				else {
					isRunning = false;
					lock.unlock();
				}
				break;

			case SHUT_DOWN:
				break;
			}
		}
		else {
			lock.unlock();
		}
	}


	/**
	 * 
	 */
	public void requestSave() {
		lock.lock();
		isSavingRequired = true;
		lock.unlock();

		transition(false, null);
	}


	/**
	 * 
	 */
	public void requestShutDown(C0Callback callback) {
		if(isVerbose) {
			System.out.print("[SHUTTING-DOWN sequence]: resource shut-down... "
					+ "state="+state);
			System.out.print("\n");
			
		}
	
		DEBUG_log += "|shut-down required";
		
		
		lock.lock();
		isShuttingDownRequired = true;
		terminationCallback = callback;
		lock.unlock();

		transition(false, null);
	}




	void rerouteQueue() {
		H2Operator<M> operator;
		while((operator = queue.poll()) != null) {
			reroute(operator);
		}
	}


	void load() throws IOException {
		model = getPrototype().load();
		updateBytecount();
	}


	void save() throws IOException {
		getPrototype().save(model);
	}

	public long getBytecount() throws IOException {
		return bytecount;
	}


	/**
	 * It is the responsibility of the prototype getBytecount method to efficiently cache byte count computations
	 */
	public void updateBytecount() {
		try {
			bytecount = getPrototype().getBytecount(model);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 */
	public abstract void detach();


	/**
	 * <p>Must handle the following actions:</p>
	 * <ul>
	 * <li><b>detach()</p>: Remove shell handler from so stop incoming flow of requests</li>
	 * <li>Recursively remove all sub hadnler</li>
	 * </ul>
	 * 				
	 * @param callback
	 * 
	 */


	public abstract void reroute(H2Operator<M> operator);



	public void terminate() {

		// switch state (directly reroute)
		state = State.SHUT_DOWN;

		/**
		 * detach
		 */
		detach();

		/*
		 * then reroute all callbacks that accumulated between shut down and handler removal from store map
		 */
		rerouteQueue();
		
		if(terminationCallback != null) {
			terminationCallback.onTerminated(C0Callback.SUCCESSFUL);
		}
	}
	
}
