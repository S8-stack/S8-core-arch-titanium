package com.s8.arch.magnesium.databases.repo.repository;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.databases.repo.store.MgRepoStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.magnesium.handlers.h3.H3MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends H3MgHandler<MgRepository> {
	
	
	private final IOModule ioModule = new IOModule(this);
	
	public final MgRepoStore store;
	
	public final String address;
	
	public final Path path;

	
	public MgRepositoryHandler(SiliconEngine ng, MgRepoStore store, String address) {
		super(ng);
		this.store = store;
		this.address = address;
		this.path = store.composeRepositoryPath(address);
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
	public void getSubUnmountables(List<H3MgUnmountable> unmountables) {
		MgRepository repository = getResource();
		if(repository != null) { repository.crawl(unmountables); }
	}


	public Path getPath() {
		return path;
	}

	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commit(long t, String branchId, NdObject[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CommitOp(t, this, branchId, objects, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(long t, String branchId, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneHeadOp(t, this, branchId, onSucceed, onFailed));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long t, String branchId, long version, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneVersionOp(t, this, branchId, version, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, String branchId, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new RetrieveHeadVersion(t, this, branchId, onSucceed, onFailed));
	}

}
