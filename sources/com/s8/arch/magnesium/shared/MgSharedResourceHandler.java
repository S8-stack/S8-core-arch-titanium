package com.s8.arch.magnesium.shared;

import java.util.ArrayDeque;
import java.util.Queue;

import com.s8.arch.magnesium.callbacks.VoidMgCallback;
import com.s8.arch.silicon.SiliconEngine;

/**
 * 
 * @author pierreconvert
 *
 */
public abstract class MgSharedResourceHandler<R> {


	public enum Status {
		UNMOUNTED, LOADED, FAILED;
	}

	public abstract String getName();

	/**
	 * Internal lock
	 */
	private final Object lock = new Object();


	/**
	 * Status of the handler
	 */
	private Status status = Status.UNMOUNTED;


	/**
	 * Timestamp of the last operation
	 */
	private volatile long lastOpTimestamp;

	private volatile boolean isActive = false;


	private volatile boolean isSaved = false;


	/**
	 * In cas eth handler failed to load the resources
	 */
	private Exception exception;


	/** 
	 * The resoucre the handler can load
	 */
	R resource;



	/**
	 * 
	 */
	private Queue<MgOperation<R>> operations = new ArrayDeque<>();



	private SiliconEngine ng;


	public MgSharedResourceHandler() {
		super();
	}


	/**
	 * 
	 * @param ng
	 */
	public void initialize(SiliconEngine ng) {
		this.ng = ng;
	}


	boolean isSaved() {
		/* volatile implementation of the field */
		return isSaved;
	}


	void notifySuccessfullySaved(){
		isSaved = true;
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
		pushOperation(new SaveOp<>(this));
	}


	/**
	 * 
	 */
	public void unmount(long cutOffTimestamp, VoidMgCallback callback) {
		pushOperation(new UnmountOp<>(this, cutOffTimestamp, callback));
	}
	
	
	
	public abstract void unmountResource(VoidMgCallback callback);
	


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
	protected void pushOperation(MgOperation<R> operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.add(operation);

		}

		/* launch rolling */
		roll(false);

	}




	/**
	 * 
	 * @return
	 */
	public abstract MgIOModule<R> getIOModule();



	/**
	 * 
	 * @param resource
	 */
	public void setResource(R resource) {

		/* low-contention probability synchronized section */
		synchronized (lock) {
			this.resource = resource;
			this.status = Status.LOADED;
		}
	}


	/**
	 * 
	 * @param exception
	 */
	public void raiseException(Exception exception) {

		/* low-contention probability synchronized section */
		synchronized (lock) {
			this.exception = exception;
			this.status = Status.FAILED;
		}
	}



	/**
	 * 
	 * @return
	 */
	public boolean isRolling() {
		synchronized (lock) { return isActive; }
	}

	public Status getStatus() {
		synchronized (lock) { return status; }
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

		synchronized (lock) {

			if(isActive == isContinued) {

				switch(status) {

				case UNMOUNTED:
					if(!operations.isEmpty()) { // has operations to perform
						ng.pushAsyncTask(new LoadMgTask<R>(this));
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
						MgOperation<R> operation = operations.poll();

						/* update resource modification status */
						if(!operation.isReadOnly()) { isSaved = false; }

						/* update timestamp */
						if(operation.isUserInduced()) { lastOpTimestamp = operation.getTimestamp(); }						

						ng.pushAsyncTask(operation.createConsumeResourceTask(resource));
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

						MgOperation<R> operation = operations.poll();
						ng.pushAsyncTask(operation.createCatchExceptionTask(exception));
					}

					break;
				}
			}
		}
	}

}
