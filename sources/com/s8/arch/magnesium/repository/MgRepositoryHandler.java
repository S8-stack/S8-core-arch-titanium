package com.s8.arch.magnesium.repository;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.handler.MgHandler;
import com.s8.arch.magnesium.handler.MgIOModule;
import com.s8.arch.magnesium.handler.MgUnmountable;
import com.s8.arch.magnesium.store.MgStore;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends MgHandler<MgRepository> {
	
	
	private final IOModule ioModule = new IOModule(this);
	
	public final MgStore store;
	
	public final String address;
	
	public final Path path;

	
	public MgRepositoryHandler(SiliconEngine ng, MgStore store, String address) {
		super(ng);
		this.store = store;
		this.address = address;
		this.path = store.composeRepositoryPath(address);
	}

	
	/**
	 * 
	 * @return
	 */
	public MgStore getStore() {
		return store;
	}


	@Override
	public String getName() {
		return "repository handler of: "+address;
	}

	@Override
	public MgIOModule<MgRepository> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<MgUnmountable> unmountables) {
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
	public void commit(long t, String branchName, NdObject[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CommitOp(t, this, branchName, objects, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(long t, String branchName, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneHeadOp(t, this, branchName, onSucceed, onFailed));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long t, String branchName, long version, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneVersionOp(t, this, branchName, version, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, String branchName, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new RetrieveHeadVersion(t, this, branchName, onSucceed, onFailed));
	}

}
