package com.s8.core.arch.magnesium.databases.space.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.api.objects.repo.RepoS8Object;
import com.s8.core.arch.magnesium.databases.space.entry.MgSpaceHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.bohr.lithium.codebase.LiCodebase;


/**
 * 
 * @author pierreconvert
 *
 */
public class SpaceMgStore {


	public final SpaceMgDatabase handler;

	public final LiCodebase codebase;

	public final SpaceMgStoreMetadata metadata;
	
	

	private Path path;

	public final MgPathComposer pathComposer;

	public final Map<String, MgSpaceHandler> spaceHandlers = new HashMap<>();


	public SpaceMgStore(SpaceMgDatabase handler, LiCodebase codebase, SpaceMgStoreMetadata metadata) {
		super();
		this.handler = handler;
		this.codebase = codebase;
		this.metadata = metadata;

		String rootPathname = metadata.rootFolderPathname;
		this.path = Path.of(rootPathname);
		this.pathComposer = new MgPathComposer(path);
	}

	
	
	
	/**
	 * 
	 * @param spaceId
	 * @return
	 * @throws IOException
	 */
	MgSpaceHandler getSpaceHandler(String spaceId) throws IOException {
		
		MgSpaceHandler spaceHandler = spaceHandlers.get(spaceId);
		
		if(spaceHandler != null) {
			return spaceHandler;
		}
		else {
			Path dataFolderPathname = pathComposer.composePath(spaceId);
			boolean isPresent = dataFolderPathname.toFile().exists();
			if(isPresent) {
				/* Already created -> just need to create the handler and tha will automatically load it */
				MgSpaceHandler spaceHandler2 = new MgSpaceHandler(
						handler.ng, 
						this, 
						spaceId, 
						dataFolderPathname);

				spaceHandlers.put(spaceId, spaceHandler2);
				
				return spaceHandler2;
			}
			else { 
				/* cannot find an existing one and cannot create one */
				return null;
			}
		}
	}
	
	

	/**
	 * 
	 * @param spaceId
	 * @return
	 * @throws IOException
	 */
	MgSpaceHandler createSpaceHandler(String spaceId) throws IOException {
		
		MgSpaceHandler spaceHandler = spaceHandlers.get(spaceId);
		
		if(spaceHandler != null) {
			return null; /* -> conflict with already existing one */
		}
		else {
			Path dataFolderPathname = pathComposer.composePath(spaceId);
			boolean isPresent = dataFolderPathname.toFile().exists();
			if(!isPresent) {
				/* Already created -> just need to create the handler and tha will automatically load it */
				MgSpaceHandler spaceHandler2 = new MgSpaceHandler(
						handler.ng, 
						this, 
						spaceId, 
						dataFolderPathname);

				spaceHandlers.put(spaceId, spaceHandler2);
				
				return spaceHandler2;
			}
			else { 
				/* Already an existing one -> conflict -> null */
				return null;
			}
		}
	}


	

	/**
	 * 
	 * @param repositoryAddress
	 * @return
	 */
	/*
	public MgSpaceHandler getSpaceHandler(String repositoryAddress) {
		return spaceHandlers.computeIfAbsent(repositoryAddress, 
				address -> new MgSpaceHandler(
						handler.ng, 
						this, 
						repositoryAddress, 
						pathComposer.composePath(repositoryAddress)));
	}
	 */

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
		return path;
	}

	public Path composeRepositoryPath(String address) {
		return pathComposer.composePath(address);
	}


	public LiCodebase getCodebase() {
		return codebase;
	}


	/**
	 * 
	 * @param id
	 * @param name
	 */
	public void createRepository(String id, String name) {

	}

	public void commit(String repositoryId, String branchId, RepoS8Object[] objects) {

	}







	public List<H3MgHandler<?>> getSpaceHandlers() {
		List<H3MgHandler<?>> unmountables = new ArrayList<>();
		spaceHandlers.forEach((k, repo) -> unmountables.add(repo));
		return unmountables;
	}


}
