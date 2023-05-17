package com.s8.arch.magnesium.syncbase.store;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.core.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.MgIOModule;
import com.s8.arch.magnesium.core.handler.MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;


/**
 * 
 * @author pc
 *
 */
public class MgSyncStoreHandler extends MgHandler<MgSyncStore> {

	
	public final NdCodebase codebase;
	
	public final Path storeInfoPathname;
	
	public final IOModule ioModule = new IOModule(this);
	
	public MgSyncStoreHandler(SiliconEngine ng, NdCodebase codebase, Path storeInfoPathname) {
		super(ng);
		this.codebase = codebase;
		this.storeInfoPathname = storeInfoPathname;
	}

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public MgIOModule<MgSyncStore> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<MgUnmountable> unmountables) {
		MgSyncStore store = getResource();
		if(store != null) { store.crawl(unmountables); }
	}

	public Path getInfoPath() {
		return storeInfoPathname;
	}

	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commit(long t, String repoAddress, String branchName, NdObject[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CommitOp(t, this, repoAddress, branchName, objects, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(long t, String repoAddress, String branchName, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneHeadOp(t, this, repoAddress, branchName, onSucceed, onFailed));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long t, String repoAddress, String branchName, long version, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneVersionOp(t, this, repoAddress, branchName, version, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, String repoAddress, String branchName, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new RetrieveHeadVersion(t, this, repoAddress, branchName, onSucceed, onFailed));
	}
	
	
}
