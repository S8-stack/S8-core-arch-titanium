package com.s8.arch.magnesium.databases.repo.branch;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends RequestH3MgOperation<NdBranch> {



	public @Override boolean isModifyingResource() { return false; }



	public final MgBranchHandler handler;

	public final MgCallback<BranchExposureS8AsyncOutput> onProceed;

	public final long options;


	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgBranchHandler handler, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		super(timestamp);
		this.handler = handler;
		this.onProceed = onSucceed;
		this.options = options;
	}


	@Override
	public ConsumeResourceMgTask<NdBranch> createConsumeResourceTask(NdBranch branch) {
		return new ConsumeResourceMgTask<NdBranch>(branch) {

			@Override
			public H3MgHandler<NdBranch> getHandler() {
				return handler;
			}

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+handler.getIdentifier()+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(NdBranch branch) {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
				try {
					NdObject[] objects = branch.cloneHead().exposure;
					output.objects = objects;
					output.isSuccessful = true;
				}
				catch(Exception exception) { 
					exception.printStackTrace();
					output.reportException(exception);
				}
				onProceed.call(output);
			}
		};
	}

	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) { 
		return new CatchExceptionMgTask(exception) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "failed to access resource on "+handler.getName()+": catching exception";
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
