package com.s8.arch.magnesium.databases.repo.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.arch.magnesium.databases.repo.repository.MgRepository;
import com.s8.arch.magnesium.databases.repo.repository.MgRepositoryHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;
import com.s8.io.joos.types.JOOS_CompilingException;


/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepoStore {
	
	
	public final RepoMgDatabase handler;
	
	public final NdCodebase codebase;
	
	private String rootPathname;
	
	private Path rootPath;
	
	public final MgPathComposer repoPathComposer;
	
	private final Map<String, MgRepositoryHandler> repositoryHandlers = new HashMap<>();
	
	
	public MgRepoStore(RepoMgDatabase handler, NdCodebase codebase, String rootPathname) {
		super();
		this.handler = handler;
		this.codebase = codebase;
		
		this.rootPathname = rootPathname;
		this.rootPath = Path.of(rootPathname);
		this.repoPathComposer = new MgPathComposer(rootPath);
	}
	
	
	
	
	/**
	 * 
	 * @param repositoryAddress
	 * @return
	 * @throws JOOS_CompilingException 
	 * @throws IOException 
	 */
	
	MgRepositoryHandler getRepositoryHandler(String repositoryAddress, boolean isCreateEnabled) 
			throws JOOS_CompilingException, IOException {
		MgRepositoryHandler repoHandler = repositoryHandlers.get(repositoryAddress);
		if(repoHandler != null) {
			return repoHandler;
		}
		else {
			Path dataPath = repoPathComposer.composePath(repositoryAddress);
			boolean hasBeenCreated = dataPath.toFile().exists();
			if(hasBeenCreated) {
				repoHandler = new MgRepositoryHandler(handler.ng, this, repositoryAddress);
				repositoryHandlers.put(repositoryAddress, repoHandler);
				return repoHandler;
			}
			else if(isCreateEnabled){
				
				repoHandler = new MgRepositoryHandler(handler.ng, this, repositoryAddress);
				
				MgRepository repository = new MgRepository(repositoryAddress, dataPath);
				repoHandler.initializeResource(repository);
				
				repositoryHandlers.put(repositoryAddress, repoHandler);
				return repoHandler;
			}
			else {
				return null;
			}
		}
	}
	
	
	
	
	/*
	private void JOOS_init() {
		try {
			mapLexicon = JOOS_Lexicon.from(MgRepositoryHandler.class);
		} 
		catch (JOOS_CompilingException e) {
			e.printStackTrace();
		}
	}
	*/
	
	
	public Path getRootPath() {
		return rootPath;
	}
	
	public Path composeRepositoryPath(String address) {
		return repoPathComposer.composePath(address);
	}
	

	public NdCodebase getCodebase() {
		return codebase;
	}
	
	
	/**
	 * 
	 * @param id
	 * @param name
	 */
	public void createRepository(String id, String name) {
		
	}

	public void commit(String repositoryId, String branchId, NdObject[] objects) {
		
	}
	
	
	

	
	@JOOS_Type(name = "repository")
	public static class Serialized {
		
		@JOOS_Field(name = "rootPathname") 
		public String rootPathname;
		
		
		
		public MgRepoStore deserialize(RepoMgDatabase handler, NdCodebase codebase) {
			return new MgRepoStore(handler, codebase, rootPathname);
		}
	}

	
	
	public Serialized serialize() {
		Serialized serialized = new Serialized();
		
		// address
		serialized.rootPathname = rootPathname;
		
		return serialized;
	}



	public List<H3MgHandler<?>> crawl() {
		List<H3MgHandler<?>> subHandlers = new ArrayList<>();
		repositoryHandlers.forEach((k, repo) -> subHandlers.add(repo));
		return subHandlers;
	}
	
}
