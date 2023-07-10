package com.s8.arch.magnesium.databases.repository.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.store.MgRepoStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends H3MgHandler<MgRepository> {
	
	
	private final IOModule ioModule;
	
	public final MgRepoStore store;
	
	public final String address;
	
	public final Path path;

	
	public MgRepositoryHandler(SiliconEngine ng, MgRepoStore store, String address) throws JOOS_CompilingException {
		super(ng);
		this.store = store;
		this.address = address;
		this.path = store.composeRepositoryPath(address);
		ioModule = new IOModule(this);
	}

	
	/**
	 * 
	 * @return
	 */
	public MgRepoStore getStore() {
		return store;
	}


	@Override
	public String getName() {
		return "repository handler of: "+address;
	}

	@Override
	public H3MgIOModule<MgRepository> getIOModule() {
		return ioModule;
	}

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		MgRepository repository = getResource();
		if(repository != null) { 
			return repository.crawl();
		}
		else {
			return new ArrayList<>();
		}
	}


	public Path getPath() {
		return path;
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepo(long t, S8User initiator,
			String originBranchId, long originBranchVersion, 
			MgRepositoryHandler targetRepositoryHandler,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, long options) {
		pushOperation(new ForkRepoOp(t, initiator, this, 
				originBranchId, originBranchVersion, 
				targetRepositoryHandler, 
				onSucceed, options));
	}
	

	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator,
			String originBranchId, long originBranchVersion, String targetBranchId,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, long options) {
		pushOperation(new ForkBranchOp(t, initiator, this, originBranchId, originBranchVersion, targetBranchId, onSucceed, options));
	}
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, 
			String branchId, NdObject[] objects, String comment,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CommitBranchOp(t, initiator, this, branchId, objects, comment, onSucceed, options));
	}




	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, 
			String branchId, long version, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CloneBranchOp(t, initiator, this, branchId, version, onSucceed, options));
	}


	/**
	 * 
	 * @param headVersion
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, S8User initiator, String branchId, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new RetrieveHeadVersion(t, initiator, this, branchId, onSucceed, options));
	}

}
