package com.s8.arch.magnesium.handlers.h3;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.core.arch.silicon.SiliconEngine;

/**
 * 
 * @author pierreconvert
 *
 */
public abstract class H3MgHandler<R> {


	public enum Status {
		UNMOUNTED, LOADED, FAILED;
	}
	

	public abstract String getName();

	/**
	 * Internal lock
	 */
	final Object lock = new Object();


	/**
	 * Status of the handler
	 */
	volatile Status status = Status.UNMOUNTED;


	/**
	 * Timestamp of the last operation
	 */
	private volatile long lastOpTimestamp;

	private volatile boolean isActive = false;


	volatile boolean isSaved = false;


	/**
	 * In cas eth handler failed to load the resources
	 */
	Exception exception;


	/** 
	 * (access only via synchronized)
	 * The resoucre the handler can load
	 */
	R resource;



	/**
	 * 
	 */
	private Deque<H3MgOperation<R>> operations = new ArrayDeque<>();



	public final SiliconEngine ng;


	public H3MgHandler(SiliconEngine ng) {
		super();
		this.ng = ng;
	}




	/**
	 * 
	 * @param cutOffTimestamp
	 * @return
	 */
	public boolean isDetachable(long cutOffTimestamp) {
		synchronized (lock) { 
			return // MUST be ...

					/* inactive for a certain time */
					lastOpTimestamp < cutOffTimestamp &&

					/* All changes must have been saved */
					isSaved &&

					/* Should not have activity at the moment */
					!isActive && 

					/* no pending operations */
					operations.isEmpty();
		}
	}




	/* launch rolling */
	public void save() {
		pushOpLast(new SaveOp<>(this));
	}
	
	public void saveImmediately() {
		pushOpFirst(new SaveOp<>(this));
	}


	
	/**
	 * 
	 */
	public void unmount(long cutOffTimestamp, BooleanMgCallback onUnmounted) {
		pushOpLast(new UnmountOp<>(this, cutOffTimestamp, onUnmounted));
	}
	
	


	/**
	 * 
	 * @return
	 */
	public long getLastOpTimestamp() {
		/* volatile variable so no lock-sync needed */
		return lastOpTimestamp;
	}





	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOpFirst(H3MgOperation<R> operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.addFirst(operation);

		}

		/* launch rolling */
		roll(false);

	}
	
	
	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	protected void pushOpLast(H3MgOperation<R> operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.addLast(operation);

		}

		/* launch rolling */
		roll(false);

	}




	/**
	 * 
	 * @return
	 */
	public abstract H3MgIOModule<R> getIOModule();

	

	/**
	 * 
	 * @param resource
	 * @throws IOException 
	 */
	public void initializeResource(R resource) throws IOException {

		/* low-contention probability synchronized section */
		synchronized (lock) {
			if(status == Status.UNMOUNTED) {
				this.resource = resource;
				this.status = Status.LOADED;
				this.isSaved = false;	
			}
			else {
				throw new IOException("Handler state cannot be initialized");
			}
		}
	}


	
	
	
	
	
	public void setUnmounted() {
		/* low-contention probability synchronized section */
		synchronized (lock) {
			this.resource = null;
			this.exception = null;
			this.status = Status.UNMOUNTED;
		}
	}



	/**
	 * 
	 * @return
	 */
	public R getResource() {
		synchronized (lock) { return resource; }
	}
	
	

	/**
	 * 
	 * @return
	 */
	public boolean isRolling() {
		synchronized (lock) { return isActive; }
	}

	
	/**
	 * 
	 * @return
	 */
	public Status getStatus() {
		return status;
	}


	/**
	 * Should nto be called when transitioning
	 */
	void roll(boolean isContinued) {

		/* 
		 * Start rolling if not already rolling. Two cases:
		 * <ul>
		 * <li>Called by pushOperation() -> initial start : !isContinued: proceed only if !isRolling</li>
		 * <li>Called by MgBranchOperation() -> continuation : (isContinued == true): proceed only if isRolling == true</li>
		 * </ul>
		 * => Almost equal to re-entrant lock
		 */

		/* <low-contention synchronized block> */
		synchronized (lock) {

			if(isActive == isContinued) {

				switch(status) {

				case UNMOUNTED:
					if(!operations.isEmpty()) { // has operations to perform
						isActive = true;
						ng.pushAsyncTask(new LoadMgAsyncTask<R>(this));
						/*
						 * Immediately exit Syncchronized block after pushing the task
						 * --> Leave time to avoid contention
						 */
					}
					break;

				case LOADED:
					/**
					 * Get next operation to be performed
					 */
					if(!operations.isEmpty()) {
						
						/* force active status (might have entered has not active) */
						isActive = true;
						
						H3MgOperation<R> operation = operations.poll();


						/* update timestamp */
						if(operation.isUserInduced()) { lastOpTimestamp = operation.getTimestamp(); }						

						ng.pushAsyncTask(operation.createAsyncTask());
						/*
						 * Immediately exit Syncchronized block after pushing the task
						 * --> Leave time to avoid contention
						 */
					}
					else { // queue.isEmpty()
						isActive = false; // close rolling
					}
					break;

				case FAILED:

					isActive = false; // close rolling

					// flush queue (can be concurrent flushing at this point)
					while(!operations.isEmpty()) {

						H3MgOperation<R> operation = operations.poll();
						ng.pushAsyncTask(operation.createAsyncTask());
					}

					break;
				}
			}
		}
		/* </low-contention synchronized block> */
	}

	
	
	public abstract List<H3MgHandler<?>> getSubHandlers();


	/* <MgTask interface> */

	

	boolean isSaved() {
		synchronized (lock) { return isSaved; }
	}
	
	
	void notifySuccessfullySaved() {
		synchronized (lock) {  isSaved = true; }	
	}
	
	
	/**
	 * 
	 * @param resource
	 */
	public void setLoaded(R resource) {
		/* low-contention probability synchronized section */
		synchronized (lock) {
			this.resource = resource;
			status = Status.LOADED;
			isSaved = true;
		}
	}
	
	
	/**
	 * 
	 * @param e
	 */
	void setFailed(Exception e) {
		/* low-contention probability synchronized section */
		synchronized (lock) {
			exception = e;
			status = Status.FAILED;
		}		
	}

	
	

}
