package com.s8.arch.magnesium.branches;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class Commit extends MgBranchOperation {

	

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
	public Commit(MgBranchHandler handler, NdObject[] objects, VersionMgCallback onSucceed, ErrorMgCallback onFailed) {
		super(handler, onFailed);
		this.objects = objects;
		this.onSucceed = onSucceed;
	}


	@Override
	public AsyncTask createTask() {
		return new AsyncTask() {

			@Override
			public void run() {

				try {
					long version = handler.branch.commit(objects);
					onSucceed.onUpdate(version);
				}
				catch(Exception exception) {
					onFailed.onRaised(exception.getMessage());
				}

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
