package com.s8.arch.magnesium.databases.repository.branch;

import java.io.IOException;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends RequestH3MgOperation<NdBranch> {


	public final MgBranchHandler branchHandler;

	public final MgCallback<BranchExposureS8AsyncOutput> onProceed;

	public final long options;


	/**
	 * 
	 * @param branchHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgBranchHandler branchHandler, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		super(timestamp);
		this.branchHandler = branchHandler;
		this.onProceed = onSucceed;
		this.options = options;
	}


	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return branchHandler;
	}



	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(branchHandler) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchHandler.getIdentifier()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws IOException, S8ShellStructureException {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();

				NdObject[] objects = branch.cloneHead().exposure;
				output.objects = objects;
				output.isSuccessful = true;

				onProceed.call(output);
				return false; // Clone option has no effect on resource
			}

			@Override
			public void catchException(Exception exception) {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
				output.reportException(exception);
				onProceed.call(output);
			}
		};
	}

}
