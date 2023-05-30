package com.s8.arch.magnesium.handlers.h3;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.arch.silicon.SiliconEngine;

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
	private final Object lock = new Object();


	/**
	 * Status of the handler
	 */
	private volatile Status status = Status.UNMOUNTED;


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
	private Queue<H3MgOperation<R>> operations = new ArrayDeque<>();



	public final SiliconEngine ng;


	public H3MgHandler(SiliconEngine ng) {
		super();
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
	public void unmount(long cutOffTimestamp, BooleanMgCallback onUnmounted) {
		pushOperation(new UnmountOp<>(this, cutOffTimestamp, onUnmounted));
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
	protected void pushOperation(H3MgOperation<R> operation) {

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


	/**
	 * 
	 * @param resource
	 */
	public void setLoaded(R resource) {

		/* low-contention probability synchronized section */
		synchronized (lock) {
			this.resource = resource;
			this.status = Status.LOADED;
			this.isSaved = true;
		}
	}


	/**
	 * 
	 * @param exception
	 */
	public void setFailed(Exception exception) {

		/* low-contention probability synchronized section */
		synchronized (lock) {
			this.exception = exception;
			this.status = Status.FAILED;
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
		synchronized (lock) {
			return resource;	
		}
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

		synchronized (lock) {

			if(isActive == isContinued) {

				switch(status) {

				case UNMOUNTED:
					if(!operations.isEmpty()) { // has operations to perform
						isActive = true;
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
						isActive = true;
						H3MgOperation<R> operation = operations.poll();

						/* update resource modification status */
						if(operation.isModifyingResource()) { isSaved = false; }

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

						H3MgOperation<R> operation = operations.poll();
						ng.pushAsyncTask(operation.createCatchExceptionTask(exception));
					}

					break;
				}
			}
		}
	}

	
	
	public abstract List<H3MgHandler<?>> getSubHandlers();
	
}
