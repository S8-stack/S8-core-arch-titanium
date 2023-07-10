package com.s8.arch.magnesium.databases.repository.branch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.entry.MgBranchMetadata;
import com.s8.arch.magnesium.databases.repository.entry.MgRepository;
import com.s8.arch.magnesium.databases.repository.store.MgRepoStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */

public class MgBranchHandler extends H3MgHandler<NdBranch> {

	public final static String BRANCH_DATA_FILENAME = "branch-data.nd";

	public MgBranchMetadata metadata;
	
	
	

	
	
	public String getIdentifier() {
		return metadata.name;
	}
	
	
	public long getVersion() {
		return metadata.headVersion;
	}
	

	@Override
	public String getName() {
		return metadata.name;
	}


	
	public final static String DEFAULT_BRANCH_NAME = "prime";

	
	/*
	public static MgBranchHandler create(SiliconEngine ng, MgRepoStore store, MgRepository repository, String name) {

		String id = DEFAULT_BRANCH_NAME;
		
		MgBranchHandler branchHandler = new MgBranchHandler(ng, store, repository);
	
		NdCodebase codebase = store.getCodebase();
		
		branchHandler.id = id;
		branchHandler.name = name;

		branchHandler.setLoaded(new NdBranch(codebase, id));
		branchHandler.save();

		return branchHandler;
	}
	*/
	


	public final MgRepoStore store;

	public final MgRepository repository;


	private final H3MgIOModule<NdBranch> ioModule = new IOModule(this);

	public MgBranchHandler(SiliconEngine ng, MgRepoStore store, MgRepository repository, MgBranchMetadata metadata) {
		super(ng);
		this.store = store;
		this.repository = repository;
		this.metadata = metadata;
	}



	/**
	 * 
	 * @return
	 */
	public MgRepoStore getStore() {
		return store;
	}

	

	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, NdObject[] objects, String comment, 
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CommitBranchOp(t, initiator, this, objects, comment, onSucceed, options));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, long version, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CloneBranchOp(t, initiator, this, version, onSucceed, options));
	}
	
	
	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, 
			long version, MgBranchHandler targetBranchHandler, 
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, long options) {
		pushOperation(new ForkBranchOp(t, initiator, this, version, targetBranchHandler, onSucceed, options));
	}


	/**
	 * 
	 * @param headVersion
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, S8User initiator, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new RetrieveHeadVersion(t, initiator, this, onSucceed, options));
	}


	/**
	 * 
	 * @return path to repository branch sequence
	 */
	Path getFolderPath() {
		return repository.getFolderPath().resolve(metadata.name);
	}
	
	Path getDataFilePath() {
		return getFolderPath().resolve(BRANCH_DATA_FILENAME);
	}



	@Override
	public H3MgIOModule<NdBranch> getIOModule() {
		return ioModule;
	}


	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		return new ArrayList<>();
	}

}
