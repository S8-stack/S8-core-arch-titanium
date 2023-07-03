package com.s8.arch.magnesium.databases.repository.branch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.entry.MgRepository;
import com.s8.arch.magnesium.databases.repository.store.MgRepoStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
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

public class MgBranchHandler extends H3MgHandler<NdBranch> {


	private String id;

	private String name;

	private long version;
	
	
	@JOOS_Type(name = "branch")
	public static class Serialized {
		
		@JOOS_Field(name = "id")
		public String id;


		@JOOS_Field(name = "name")
		public String name;

		@JOOS_Field(name = "version")
		public long version;
		
		
		public MgBranchHandler deserialize(SiliconEngine ng, MgRepoStore store, MgRepository repository) {
			MgBranchHandler handler = new MgBranchHandler(ng, store, repository);
			handler.id = id;
			handler.name = name;
			handler.version = version;
			return handler;
		}
		
	}

	
	
	public String getIdentifier() {
		return id;
	}
	
	public long getVersion() {
		return version;
	}

	
	public final static String DEFAULT_BRANCH_NAME = "prime";

	
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
	


	public final MgRepoStore store;

	public final MgRepository repository;


	private final H3MgIOModule<NdBranch> ioModule = new IOModule(this);

	public MgBranchHandler(SiliconEngine ng, MgRepoStore store, MgRepository repository) {
		super(ng);
		this.store = store;
		this.repository = repository;
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
	public void commit(long t, NdObject[] objects, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CommitOp(t, this, objects, onSucceed, options));
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneHead(long t, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CloneHeadOp(t, this, onSucceed, options));
	}



	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneVersion(long t, long version, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CloneVersionOp(t, this, version, onSucceed, options));
	}


	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new RetrieveHeadVersion(t, this, onSucceed, options));
	}


	/**
	 * 
	 * @return path to repository branch sequence
	 */
	Path getPath() {
		return repository.getPath().resolve(id);
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public H3MgIOModule<NdBranch> getIOModule() {
		return ioModule;
	}


	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		return new ArrayList<>();
	}

	
	public Serialized serialize() {
		Serialized serializable = new Serialized();
		serializable.id = id;
		serializable.name = name;
		serializable.version = version;
		return serializable;
	}
}
