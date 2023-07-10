package com.s8.arch.magnesium.databases.repository.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.RepoCreationS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.arch.magnesium.databases.repository.branch.MgBranchHandler;
import com.s8.arch.magnesium.databases.repository.entry.MgBranchMetadata;
import com.s8.arch.magnesium.databases.repository.entry.MgRepository;
import com.s8.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.arch.magnesium.databases.repository.entry.MgRepositoryMetadata;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class CreateRepoOp extends RequestDbMgOperation<MgRepoStore> {


	public final RepoMgDatabase storeHandler;

	public final String repositoryAddress;
	
	public final String repositoryInfo;
	
	public final String mainBranchName;
	
	public final NdObject[] objects;
	
	public final String initialCommitComment;

	public final S8User initiator;
	
	public final MgCallback<RepoCreationS8AsyncOutput> onSucceed;



	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CreateRepoOp(long timestamp, S8User initiator,
			RepoMgDatabase handler, 
			String repositoryAddress,
			String repositoryInfo, 
			String mainBranchName, 
			NdObject[] objects, 
			String initialCommitComment,
			MgCallback<RepoCreationS8AsyncOutput> onSucceed, 
			long options) {
		super(timestamp, initiator, options);
		this.storeHandler = handler;
		this.repositoryAddress = repositoryAddress;
		this.repositoryInfo = repositoryInfo;
		this.mainBranchName = mainBranchName;
		this.objects = objects;
		this.initialCommitComment = initialCommitComment;
		this.initiator = initiator;
		this.onSucceed = onSucceed;
	}


	@Override
	public H3MgHandler<MgRepoStore> getHandler() {
		return storeHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<MgRepoStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<MgRepoStore>(storeHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(MgRepoStore store) throws JOOS_CompilingException, IOException, S8ShellStructureException {


				MgRepositoryHandler repoHandler = store.createRepositoryHandler(repositoryAddress);

				if(repoHandler != null) {
					
					/* <metadata> */
					MgRepositoryMetadata metadata = new MgRepositoryMetadata();
					metadata.address = repositoryAddress;
					metadata.info = repositoryInfo;
					metadata.owner = initiator.getUsername();
					metadata.creationDate = timeStamp;
					metadata.branches = new HashMap<>();
					
					/* define a new (main) branch */
					MgBranchMetadata mainBranchMetadata = new MgBranchMetadata();
					mainBranchMetadata.name = mainBranchName;
					mainBranchMetadata.info = "Created as MAIN branch";
					mainBranchMetadata.headVersion = 0L;
					mainBranchMetadata.forkedBranchId = null;
					mainBranchMetadata.forkedBranchVersion = 0L;
					mainBranchMetadata.owner = initiator.getUsername();
					metadata.branches.put(mainBranchName, mainBranchMetadata);
					
					/* </metadata> */
					
					SiliconEngine ng = handler.ng;

					Path path = store.composeRepositoryPath(metadata.address);
					
					
					
					MgRepository repository = new MgRepository(metadata, path);
					
					/* <nd-branch> */
					MgBranchHandler branchHandler = new MgBranchHandler(ng, store, repository, mainBranchMetadata);
					NdBranch ndBranch = new NdBranch(store.getCodebase(), mainBranchName);
					ndBranch.commit(objects, getTimestamp(), initiator.getUsername(), initialCommitComment);
					branchHandler.initializeResource(ndBranch);
					/* </nd-branch> */
					
					repository.branchHandlers.put(mainBranchName, branchHandler);
					repoHandler.initializeResource(repository);
					
					
					RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
					output.isSuccessful = true;
					output.hasAddressConflict = false;
					onSucceed.call(output);
					return true;
				}
				else {
					/* if repoHandler is null => implies collision for repository address */
					RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
					output.isSuccessful = false;
					output.hasAddressConflict = true;
					onSucceed.call(output);
					return false;
				}
			}

			@Override
			public void catchException(Exception exception) {
				RepoCreationS8AsyncOutput output = new RepoCreationS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}
		};
	}


}
