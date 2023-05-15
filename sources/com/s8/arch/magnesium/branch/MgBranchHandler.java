package com.s8.arch.magnesium.branch;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.handler.MgIOModule;
import com.s8.arch.magnesium.handler.MgHandler;
import com.s8.arch.magnesium.handler.MgUnmountable;
import com.s8.arch.magnesium.repository.MgRepositoryHandler;
import com.s8.arch.magnesium.store.MgStore;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JOOS_Type(name = "branch")
public class MgBranchHandler extends MgHandler<NdBranch> {


	@JOOS_Field(name = "id")
	public String id;


	@JOOS_Field(name = "name")
	public String name;

	
	@JOOS_Field(name = "version")
	public long version;


	
	public final static String DEFAULT_BRANCH_NAME = "prime";

	
	public static MgBranchHandler create(MgStore store, String name) {

		String id = DEFAULT_BRANCH_NAME;
		
		MgBranchHandler branchHandler = new MgBranchHandler();
		branchHandler.initialize(store.getEngine());
	
		NdCodebase codebase = store.getCodebase();
		
		branchHandler.id = id;
		branchHandler.name = name;

		branchHandler.setLoaded(new NdBranch(codebase, id));
		branchHandler.save();

		return branchHandler;
	}
	


	private MgStore store;

	public MgRepositoryHandler repository;


	private final MgIOModule<NdBranch> ioModule = new MgBranchIO(this);

	public MgBranchHandler() {
		super();
	}



	public void link(SiliconEngine ng, MgStore store) {
		initialize(ng);
		this.store = store;
	}


	/**
	 * 
	 * @return
	 */
	public MgStore getStore() {
		return store;
	}

	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commit(long t, NdObject[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CommitOp(t, this, objects, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(long t, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneHeadOp(t, this, onSucceed, onFailed));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long t, long version, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new CloneVersionOp(t, this, version, onSucceed, onFailed));
	}


	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new RetrieveHeadVersion(t, this, onSucceed, onFailed));
	}


	/**
	 * 
	 * @return path to repository branch sequence
	 */
	Path getPath() {
		return repository.path.resolve(id);
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public MgIOModule<NdBranch> getIOModule() {
		return ioModule;
	}


	@Override
	public void getSubUnmountables(List<MgUnmountable> unmountables) {
		// no sub handlers
	}

}
