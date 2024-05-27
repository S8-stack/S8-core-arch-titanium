package com.s8.core.arch.magnesium.db;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

import com.s8.core.arch.magnesium.db.requests.AccessMgRequest;
import com.s8.core.arch.magnesium.db.requests.CreateMgRequest;
import com.s8.core.arch.magnesium.db.requests.DeleteMgRequest;
import com.s8.core.arch.magnesium.db.requests.MgRequest;
import com.s8.core.arch.silicon.SiliconEngine;

/**
 * 
 * @author pierreconvert
 *
 */
class MgDbHandler<R> {



	public final SiliconEngine ng;

	public final MgDbSwitcher<R> switcher;
	
	public final String key;
	
	public final Path path;


	/**
	 * Internal lock
	 */
	final Object lock = new Object();


	/**
	 * Status of the handler
	 * (null : undetermined)
	 */
	volatile MgResourceStatus resourceStatus = null;


	/**
	 * Timestamp of the last operation
	 */
	private volatile long lastOpTimestamp;

	private volatile boolean isActive = false;




	/**
	 * is sync
	 */
	volatile boolean isSynced = true;



	//volatile boolean isSaved = false;



	/** 
	 * (access only via synchronized)
	 * The resoucre the handler can load
	 */
	volatile R resource;



	/**
	 * 
	 */
	private Deque<MgOperation<R>> operations = new ArrayDeque<>();





	public MgDbHandler(SiliconEngine ng, MgDbSwitcher<R> switcher, String key) {
		super();
		this.ng = ng;
		this.switcher = switcher;
		this.key = key;
		this.path = switcher.pathComposer.composePath(key);
		
		this.resourceStatus = null;
	}



	/**
	 * 
	 * @return
	 */
	public boolean isResourceAvailable() {
		return resourceStatus != null && resourceStatus.isAvailable();
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

					/* status MUST be compatible with detaching */
					isSynced &&

					/* Should not have activity at the moment */
					!isActive && 

					/* no pending operations */
					operations.isEmpty();
		}
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
	void pushOpFirst(MgOperation<R> operation) {

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
	void pushOpLast(MgOperation<R> operation) {

		/* low contention synchronized section */
		synchronized (lock) {

			/* enqueue operation */
			operations.addLast(operation);

		}

		/* launch rolling */
		roll(false);

	}



	/* launch rolling */
	public void save() {
		pushOpLast(new SaveOp<>(this));
	}


	/**
	 * save immediately
	 */
	public void saveImmediately() {
		pushOpFirst(new SaveOp<>(this));
	}



	public void processRequest(MgRequest<R> request) {
		switch(request.getType()) {

		case CREATE : 
			pushOpLast(new CreateRequestOp<>(this, (CreateMgRequest<R>) request));
			break;

		case ACCESS : 
			pushOpLast(new AccessRequestOp<>(this, (AccessMgRequest<R>) request));
			break;

		case DELETE : 
			pushOpLast(new DeleteRequestOp<>(this, (DeleteMgRequest<R>) request));
			break;

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


				/* force active status (might have entered has not active) */
				isActive = true;

				MgOperation<R> operation = operations.poll();

				if(operation != null) {

					/* update timestamp */
					long t = operation.getTimestamp();
					if(t >= 0) { lastOpTimestamp = t; }						


					/**
					 * 
					 */
					operation.perform();

				}
				else {
					
					/* force active status (might have entered has not active) */
					isActive = false;
				}
			}
		}
		/* </low-contention synchronized block> */
	}




	/* <I/O operations> */
	

	/**
	 * Fast check of resource existence
	 * @return
	 */
	boolean io_hasResource() {
		return switcher.getIOModule().hasResource(path);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean io_loadResource() throws MgIOException {
		return (resource = switcher.getIOModule().readResource(path)) != null;
	}
	
	
	/**
	 * 
	 * @param resource
	 * @throws Exception
	 */
	void io_saveResource() throws IOException {
		switcher.getIOModule().writeResource(path, resource);
	}
	
	
	/**
	 * Fast deletion of resource existence (save disk space)
	 * @return
	 */
	boolean io_deleteResource() {
		return switcher.getIOModule().deleteResource(path);
	}


	/* </I/O operations> */

}
