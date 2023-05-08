package com.s8.arch.magnesium.branches;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHead extends MgBranchOperation {

	

	public final ObjectsMgCallback onSucceed;


	public CloneHead(MgBranchHandler handler, ObjectsMgCallback onSucceed, ErrorMgCallback onFailed) {
		super(handler, onFailed);
		this.onSucceed = onSucceed;
	}


	@Override
	public AsyncTask createTask() {
		return new AsyncTask() {

			@Override
			public void run() {

				try {
					NdObject[] objects = handler.branch.cloneHead().exposure;
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
