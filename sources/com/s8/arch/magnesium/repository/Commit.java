package com.s8.arch.magnesium.repository;

import com.s8.arch.magnesium.branch.MgBranchHandler;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class Commit extends MgRepositoryOperation {



	public final String branchId;

	/**
	 * version
	 */
	public final NdObject[] objects;


	/**
	 * on succeed
	 */
	public final VersionMgCallback onSucceed;



	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public Commit(MgRepositoryHandler handler, 
			String branchId, 
			NdObject[] objects, 
			VersionMgCallback onSucceed, 
			ExceptionMgCallback onFailed) {
		super(handler, onFailed);
		this.branchId = branchId;
		this.objects = objects;
		this.onSucceed = onSucceed;
	}


	@Override
	public AsyncTask createTask() {
		return new AsyncTask() {

			@Override
			public void run() {

				/* retrieve branch */
				MgBranchHandler branchHandler = handler.repository.branchHandlers.get(branchId);

				/* launch commit */
				branchHandler.commit(objects, onSucceed, onFailed);

				// roll
				handler.roll(true);
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+handler.name+" branch of " + 
						handler.repository.name+ " repository";
			}
		};
	}


}
