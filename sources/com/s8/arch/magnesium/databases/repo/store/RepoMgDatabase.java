package com.s8.arch.magnesium.databases.repo.store;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;


/**
 * 
 * @author pc
 *
 */
public class RepoMgDatabase extends H3MgHandler<MgRepoStore> {

	
	public final NdCodebase codebase;
	
	public final Path storeInfoPathname;
	
	public final IOModule ioModule = new IOModule(this);
	
	public RepoMgDatabase(SiliconEngine ng, NdCodebase codebase, Path storeInfoPathname) {
		super(ng);
		this.codebase = codebase;
		this.storeInfoPathname = storeInfoPathname;
	}

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public H3MgIOModule<MgRepoStore> getIOModule() {
		return ioModule;
	}
	

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		MgRepoStore store = getResource();
		if(store != null) { 
			return store.crawl(); 
		}
		else {
			return new ArrayList<>();
		}
	}
	

	public Path getInfoPath() {
		return storeInfoPathname;
	}

	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commit(long t, String repoAddress, String branchName, Object[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CommitOp(t, this, repoAddress, branchName, (NdObject[]) objects, onSucceed, onFailed));
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
