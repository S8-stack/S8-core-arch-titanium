package com.s8.arch.magnesium.databases.space.store;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.arch.magnesium.databases.space.space.MgSpaceHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;


/**
 * 
 * @author pierreconvert
 *
 */
public class SpaceMgStore {
	
	
	public final SpaceMgDatabase handler;
	
	public final LiCodebase codebase;
	
	private String rootPathname;
	
	private Path path;
	
	private MgPathComposer pathComposer;
	
	public final Map<String, MgSpaceHandler> spaceHandlers = new HashMap<>();
	
	
	public SpaceMgStore(SpaceMgDatabase handler, LiCodebase codebase, String rootPathname) {
		super();
		this.handler = handler;
		this.codebase = codebase;
		
		this.rootPathname = rootPathname;
		this.path = Path.of(rootPathname);
		this.pathComposer = new MgPathComposer(path);
	}
	
	/**
	 * 
	 * @param repositoryAddress
	 * @return
	 */
	public MgSpaceHandler getSpaceHandler(String repositoryAddress) {
		return spaceHandlers.computeIfAbsent(repositoryAddress, 
				address -> new MgSpaceHandler(
						handler.ng, 
						this, 
						repositoryAddress, 
						pathComposer.composePath(repositoryAddress)));
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

	public void commit(String repositoryId, String branchId, NdObject[] objects) {
		
	}
	
	
	

	
	@JOOS_Type(name = "repository")
	public static class Serialized {
		
		@JOOS_Field(name = "rootPathname") 
		public String rootPathname;
		
		
		
		public SpaceMgStore deserialize(SpaceMgDatabase handler, LiCodebase codebase) {
			return new SpaceMgStore(handler, codebase, rootPathname);
		}
	}

	
	
	public Serialized serialize() {
		Serialized serialized = new Serialized();
		
		// address
		serialized.rootPathname = rootPathname;
		
		return serialized;
	}



	
	public List<H3MgHandler<?>> getSpaceHandlers() {
		List<H3MgHandler<?>> unmountables = new ArrayList<>();
		spaceHandlers.forEach((k, repo) -> unmountables.add(repo));
		return unmountables;
	}
	
	
}
