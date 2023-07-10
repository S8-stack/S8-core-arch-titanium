package com.s8.arch.magnesium.databases.repository.store;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.BranchCreationS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.BranchVersionS8AsyncOutput;
import com.s8.arch.fluor.outputs.RepoCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.types.JOOS_CompilingException;


/**
 * 
 * @author pc
 *
 */
public class RepoMgDatabase extends H3MgHandler<MgRepoStore> {

	
	public final NdCodebase codebase;
	
	public final Path storeInfoPathname;
	
	public final IOModule ioModule;
	
	public RepoMgDatabase(SiliconEngine ng, NdCodebase codebase, Path storeInfoPathname) throws JOOS_CompilingException {
		super(ng);
		this.codebase = codebase;
		this.storeInfoPathname = storeInfoPathname;
		
		ioModule = new IOModule(this);
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
	public void createRepository(long t, S8User initiator,
			String repositoryAddress,
			String repositoryInfo, 
			String mainBranchName,
			NdObject[] objects,
			String initialCommitAuthor,
			MgCallback<RepoCreationS8AsyncOutput> onSucceed, 
			long options) {
		pushOperation(new CreateRepoOp(t, initiator, this, 
				repositoryAddress, repositoryInfo, 
				mainBranchName, 
				objects, initialCommitAuthor, 
				onSucceed, options));
	}
	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepository(long t, S8User initiator,
			String originRepositoryAddress,
			String originBranchId, long originBranchVersion,
			String targetRepositoryAddress,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		pushOperation(new ForkRepoOp(t, initiator, this, originRepositoryAddress, originBranchId, originBranchVersion, targetRepositoryAddress, onSucceed, options));
	}
	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, 
			String repositoryAddress, 
			String originBranchId, long originBranchVersion, String targetBranchId,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, 
			long options) {
		pushOperation(new ForkBranchOp(t, initiator, this, 
				repositoryAddress, originBranchId, originBranchVersion, targetBranchId, 
				onSucceed, options));
	}
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, String repoAddress, String branchName, 
			Object[] objects, String comment,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new CommitBranchOp(t, initiator, this, repoAddress, branchName, (NdObject[]) objects,
				comment, onSucceed, options));
	}




	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator,  String repoAddress, String branchName, long version, 
			MgCallback<BranchExposureS8AsyncOutput> onSucceed, 
			long options) {
		pushOperation(new CloneBranchOp(t, initiator, this, repoAddress, branchName, version, onSucceed, options));
	}


	/**
	 * 
	 * @param headVersion
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveBranchHeadVersion(long t, S8User initiator, String repoAddress, String branchName, 
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, 
			long options) {
		pushOperation(new RetrieveBranchHeadVersion(t, initiator, this, repoAddress, branchName, onSucceed, options));
	}
	
	
}
