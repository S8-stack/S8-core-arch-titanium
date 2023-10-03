package com.s8.arch.magnesium.databases.repository.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.BranchCreationS8AsyncOutput;
import com.s8.api.flow.outputs.BranchExposureS8AsyncOutput;
import com.s8.api.flow.outputs.BranchVersionS8AsyncOutput;
import com.s8.api.flow.outputs.RepositoryMetadataS8AsyncOutput;
import com.s8.api.objects.repo.RepoS8Object;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.repository.store.RepoMgStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends H3MgHandler<MgRepository> {
	
	public final static String METADATA_FILENAME = "repo-meta.js";
	
	
	
	private final IOModule ioModule;
	
	public final RepoMgStore store;
	
	public final String address;
	
	public final Path folderPath;

	
	public MgRepositoryHandler(SiliconEngine ng, RepoMgStore store, String address) throws JOOS_CompilingException {
		super(ng);
		this.store = store;
		this.address = address;
		this.folderPath = store.composeRepositoryPath(address);
		ioModule = new IOModule(this);
	}

	
	/**
	 * 
	 * @return
	 */
	public RepoMgStore getStore() {
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


	public Path getFolderPath() {
		return folderPath;
	}
	
	
	public Path getMetadataFilePath() {
		return folderPath.resolve(METADATA_FILENAME);
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepo(long t, S8User initiator,
			String originBranchId, long originBranchVersion, 
			MgRepositoryHandler targetRepositoryHandler, String targetRepositoryName,
			MgCallback<BranchCreationS8AsyncOutput> onSucceed, long options) {
		pushOpLast(new ForkRepoOp(t, initiator, this, 
				originBranchId, originBranchVersion, 
				targetRepositoryHandler, targetRepositoryName,
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
		pushOpLast(new ForkBranchOp(t, initiator, this, originBranchId, originBranchVersion, targetBranchId, onSucceed, options));
	}
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, 
			String branchId, RepoS8Object[] objects, String comment,
			MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOpLast(new CommitBranchOp(t, initiator, this, branchId, objects, comment, onSucceed, options));
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
		pushOpLast(new CloneBranchOp(t, initiator, this, branchId, version, onSucceed, options));
	}


	/**
	 * 
	 * @param headVersion
	 * @param onSucceed
	 * @param onFailed
	 */
	public void retrieveHeadVersion(long t, S8User initiator, String branchId, MgCallback<BranchVersionS8AsyncOutput> onSucceed, long options) {
		pushOpLast(new RetrieveHeadVersion(t, initiator, this, branchId, onSucceed, options));
	}
	
	

	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getRepositoryMetadata(long t,  S8User initiator, 
			MgCallback<RepositoryMetadataS8AsyncOutput> onRead, long options) {
		pushOpLast(new GetMetadataOp(t, initiator, this, onRead, options));
	}
	

}
