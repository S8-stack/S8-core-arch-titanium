package com.s8.arch.magnesium.databases.repository.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
	public void createBranch(long t, String branchId, MgCallback<BranchCreationS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CreateBranchOp(t, this, branchId, onSucceed, options));
	}
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commit(long t, String branchId, NdObject[] objects, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CommitOp(t, this, branchId, objects, onSucceed, options));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(long t, String branchId, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CloneHeadOp(t, this, branchId, onSucceed, options));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long t, String branchId, long version, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CloneVersionOp(t, this, branchId, version, onSucceed, options));
	}


	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, String branchId, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new RetrieveHeadVersion(t, this, branchId, onSucceed, options));
	}

}
