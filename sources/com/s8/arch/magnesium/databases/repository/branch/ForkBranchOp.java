package com.s8.arch.magnesium.databases.repository.branch;

import java.io.IOException;

import com.s8.arch.fluor.S8AsyncFlow;
import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class ForkBranchOp extends RequestH3MgOperation<NdBranch> {


	public final MgBranchHandler originBranchHandler;

	public final long version;
	
	public final MgBranchHandler targetBranchHandler;

	public final MgCallback<BranchCreationS8AsyncOutput> onSucceed;

	public final long options;


	/**
	 * 
	 * @param originBranchHandler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkBranchOp(long timestamp, S8User initiator,
			MgBranchHandler originBranchHandler, long version, MgBranchHandler targetBranchHandler,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, long options) {
		super(timestamp, initiator);
		this.originBranchHandler = originBranchHandler;
		this.version = version;
		this.targetBranchHandler = targetBranchHandler;
		this.onSucceed = onSucceed;
		this.options = options;
	}


	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return originBranchHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(originBranchHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+originBranchHandler.getIdentifier()+" branch of "+originBranchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws IOException, S8ShellStructureException {

				/* standard cases */
				NdObject[] objects = null;
				if(version >= 0L) {
					objects = branch.cloneVersion(version).exposure;
				}
				/* special cases */
				else if(version == S8AsyncFlow.HEAD_VERSION){
					objects = branch.cloneHead().exposure;
				}
				
				
				BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
				if(objects != null) {
					NdCodebase codebase = originBranchHandler.getStore().getCodebase();
					NdBranch targetBranch = new NdBranch(codebase, targetBranchHandler.getIdentifier());
					
					/* <commit> */
					targetBranch.commit(objects, 
							getTimestamp(), 
							getInitiator().getUsername(), 
							"Initial commir from FORK of "+originBranchHandler.getName());
					
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
				BranchCreationS8AsyncOutput output = new BranchCreationS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}			
		};
	}

}
