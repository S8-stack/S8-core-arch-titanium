package com.s8.arch.magnesium.repobase.store;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.arch.magnesium.core.handler.MgUnmountable;
import com.s8.arch.magnesium.repobase.repository.MgRepositoryHandler;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;


/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepoStore {
	
	
	public final MgRepoStoreHandler handler;
	
	public final NdCodebase codebase;
	
	private String rootPathname;
	
	private Path rootPath;
	
	private MgPathComposer repoPathComposer;
	
	public final Map<String, MgRepositoryHandler> repositoryHandlers = new HashMap<>();
	
	
	public MgRepoStore(MgRepoStoreHandler handler, NdCodebase codebase, String rootPathname) {
		super();
		this.handler = handler;
		this.codebase = codebase;
		
		this.rootPathname = rootPathname;
		this.rootPath = Path.of(rootPathname);
		this.repoPathComposer = new MgPathComposer(rootPath);
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
		
		
		
		public MgRepoStore deserialize(MgRepoStoreHandler handler, NdCodebase codebase) {
			return new MgRepoStore(handler, codebase, rootPathname);
		}
	}

	
	
	public Serialized serialize() {
		Serialized serialized = new Serialized();
		
		// address
		serialized.rootPathname = rootPathname;
		
		return serialized;
	}



	public void crawl(List<MgUnmountable> unmountables) {
		repositoryHandlers.forEach((k, repo) -> unmountables.add(repo));
	}
	
}
