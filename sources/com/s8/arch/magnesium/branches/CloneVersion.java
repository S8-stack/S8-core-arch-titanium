package com.s8.arch.magnesium.branches;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneVersion extends MgBranchOperation {

	

	public final long version;
	
	public final ObjectsMgCallback onSucceed;

	public CloneVersion(MgBranchHandler handler, long version, ObjectsMgCallback onSucceed, ErrorMgCallback onFailed) {
		super(handler, onFailed);
		this.version = version;
		this.onSucceed = onSucceed;
	}


	@Override
	public AsyncTask createTask() {
		return new AsyncTask() {

			@Override
			public void run() {

				try {
					NdObject[] objects = handler.branch.cloneVersion(version).exposure;
					onSucceed.onRetrieved(objects);
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
