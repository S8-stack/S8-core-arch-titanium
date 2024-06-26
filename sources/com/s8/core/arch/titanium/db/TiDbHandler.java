package com.s8.core.arch.titanium.db;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

import com.s8.core.arch.silicon.SiliconEngine;

import com.s8.core.arch.titanium.db.requests.AccessTiRequest;
import com.s8.core.arch.titanium.db.requests.CreateTiRequest;
import com.s8.core.arch.titanium.db.requests.DeleteTiRequest;
import com.s8.core.arch.titanium.db.requests.TiRequest;

/**
 * 
 * @author pierreconvert
 *
 */
class TiDbHandler<R> {



	public final SiliconEngine ng;

	public final TiDbSwitcher<R> switcher;

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
	volatile TiResourceStatus resourceStatus = TiResourceStatus.UNDEFINED;


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
	private Deque<TiOperation<R>> operations = new ArrayDeque<>();





	public TiDbHandler(SiliconEngine ng, TiDbSwitcher<R> switcher, String key) {
		super();
		this.ng = ng;
		this.switcher = switcher;
		this.key = key;
		this.path = switcher.pathComposer.composePath(key);
	}



	/**
	 * 
	 * @return
	 */
	public boolean isResourceAvailable() {
		return resourceStatus != null && resourceStatus == TiResourceStatus.OK;
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
	void pushOpFirst(TiOperation<R> operation) {

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
	void pushOpLast(TiOperation<R> operation) {

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



	public void processRequest(TiRequest<R> request) {
		switch(request.getType()) {

		case CREATE : 
			pushOpLast(new CreateRequestOp<>(this, (CreateTiRequest<R>) request));
			break;

		case ACCESS : 
			pushOpLast(new AccessRequestOp<>(this, (AccessTiRequest<R>) request));
			break;

		case DELETE : 
			pushOpLast(new DeleteRequestOp<>(this, (DeleteTiRequest<R>) request));
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

				TiOperation<R> operation = operations.poll();

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




	/* <unit-operations> */

	/**
	 * TO be used in op
	 * @throws NoResourceTiException
	 * @throws ReadFailedTiException
	 */
	void u_loadResource() {
		if(resourceStatus != TiResourceStatus.OK) {
			try {
				io_readResource();
				resourceStatus = TiResourceStatus.OK;
				isSynced = true;	
			}
			catch (NoSuchFileException e) {
				resource = null;
				resourceStatus = TiResourceStatus.NO_RESOURCE;
				isSynced = true;
			}
			catch (FileSystemException e) {
				resource = null;
				resourceStatus = TiResourceStatus.NO_RESOURCE;
				isSynced = true;
			} 
			catch (IOException e) {
				resource = null;
				resourceStatus = TiResourceStatus.FAILED_TO_LOAD;
				isSynced = true;
			}	
		}
	}




	void u_saveResource() {
		/* save resource */
		if(!isSynced) {
			/**
			 * run callback on resource
			 */
			try {
				io_saveResource();
				resourceStatus = TiResourceStatus.OK;
				isSynced = true;

			}
			catch (Exception e) {
				e.printStackTrace();
				resourceStatus = TiResourceStatus.FAILED_TO_SAVE;
				isSynced = true;
			}
		}
	}



	/* </unit-operations> */






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
	 * @throws NoResourceTiException
	 * @throws ReadFailedTiException
	 */
	void io_readResource() throws IOException {
		resource = switcher.getIOModule().readResource(path);
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
