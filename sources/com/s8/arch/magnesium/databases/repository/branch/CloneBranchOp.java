package com.s8.arch.magnesium.databases.repository.branch;

import java.io.IOException;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneBranchOp extends RequestDbMgOperation<NdBranch> {


	public final MgBranchHandler branchHandler;

	public final long version;

	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;


	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneBranchOp(long timestamp, S8User initiator,
			MgBranchHandler handler, long version, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		super(timestamp, initiator, options);
		this.branchHandler = handler;
		this.version = version;
		this.onSucceed = onSucceed;
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
				return "CLONE-HEAD on "+branchHandler.getIdentifier()+" branch of "+branchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws IOException, S8ShellStructureException {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();

				/* standard cases */
				if(version >= 0L) {
					NdObject[] objects = branch.cloneVersion(version).exposure;
					output.objects = objects;
					output.isSuccessful = true;
				}
				/* special cases */
				else if(version == S8AsyncFlow.HEAD_VERSION){
					NdObject[] objects = branch.cloneHead().exposure;
					output.objects = objects;
					output.isSuccessful = true;
				}
				else {
					output.isSuccessful = false;
				}

				onSucceed.call(output);
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}			
		};
	}

}
