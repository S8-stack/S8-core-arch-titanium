package com.s8.arch.magnesium.branches;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.s8.arch.magnesium.stores.MgRepositoryHandler;
import com.s8.arch.magnesium.stores.MgStore;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.branch.endpoint.NdInbound;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JOOS_Type(name = "branch")
public class MgBranchHandler {


	public String id;


	@JOOS_Field(name = "name")
	public String name;

	@JOOS_Field(name = "version")
	public long version;



	private MgStore store;

	public MgRepositoryHandler repository;

	NdBranch branch;

	private String errorMessage;

	public enum Status {
		NOT_INITIATED, LOADED, FAILED, DISPOSED;
	}

	private Status status = Status.NOT_INITIATED;

	private AtomicBoolean isRolling = new AtomicBoolean(false);


	/**
	 * <p>
	 * This implementation employs an efficient <em>non-blocking</em> algorithm
	 * based on one described in
	 * <a href="http://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf">
	 * Simple, Fast, and Practical Non-Blocking and Blocking Concurrent Queue
	 * Algorithms</a> by Maged M. Michael and Michael L. Scott.
	 */
	private ConcurrentLinkedQueue<MgBranchOperation> queue = new ConcurrentLinkedQueue<>();


	public MgBranchHandler() {
		super();
	}



	public void link(MgStore store, MgRepositoryHandler repository) {
		this.store = store;
		this.repository = repository;
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commit(NdObject[] objects, VersionMgCallback onSucceed, ErrorMgCallback onFailed) {
		pushOperation(new Commit(this, objects, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(ObjectsMgCallback onSucceed, ErrorMgCallback onFailed) {
		pushOperation(new CloneHead(this, onSucceed, onFailed));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long version, ObjectsMgCallback onSucceed, ErrorMgCallback onFailed) {
		pushOperation(new CloneVersion(this, version, onSucceed, onFailed));
	}





	/**
	 * 
	 * @param engine
	 * @param operation
	 */
	private void pushOperation(MgBranchOperation operation) {


		/* enqueue */
		queue.add(operation);


		roll(false);

	}


	private Path getPath() {
		return repository.path.resolve(id);
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
		if(isRolling.compareAndSet(isContinued, !isContinued)) {

			switch(status) {

			case NOT_INITIATED:

				store.getEngine().pushAsyncTask(new AsyncTask() {
					public @Override MthProfile profile() { return MthProfile.IO_SSD; }

					@Override
					public void run() {

						try {
							/* read from disk */
							LinkedBytes head = LinkedBytesIO.read(getPath(), true);
							
							/* build inflow */
							ByteInflow inflow = new LinkedByteInflow(head);
							
							/* build inbound session */
							NdInbound inbound = new NdInbound(store.getCodebase());

							/* build branch */
							branch = new NdBranch(store.getCodebase(), "com.toto.123.098", "master");
							
							/* load branch */
							inbound.pullFrame(inflow, delta -> branch.appendDelta(delta));

							status = Status.LOADED;
							roll(true); // run another time

						} 
						catch (IOException e) {
							e.printStackTrace();

							status = Status.FAILED;
							roll(true); // run another time
						}	
					}

					@Override
					public String describe() {
						return "Load MgBranch ("+name+") ...";
					}
				});
				break;

			case LOADED:
				if(!queue.isEmpty()) {
					MgBranchOperation operation = queue.poll();
					store.getEngine().pushAsyncTask(operation.createTask());
				}
				else {
					isRolling.set(false); // close rolling
				}
				break;

			case FAILED:

				isRolling.set(false); // close rolling

				// flush queue (can be concurrent flushing at this point)
				while(!queue.isEmpty()) {
					MgBranchOperation operation = queue.poll();
					operation.onFailed.onRaised(errorMessage);
				}
				break;
			}
		}

	}

}
