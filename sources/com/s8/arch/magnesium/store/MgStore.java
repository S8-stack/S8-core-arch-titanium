package com.s8.arch.magnesium.store;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.s8.arch.magnesium.repository.MgRepositoryHandler;
import com.s8.arch.magnesium.store.config.MgConfiguration;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.codebase.NdCodebase;
import com.s8.io.bohr.neodymium.exceptions.NdBuildException;
import com.s8.io.bohr.neodymium.object.NdObject;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.types.JOOS_CompilingException;


/**
 * 
 * @author pierreconvert
 *
 */
public class MgStore {
	
	
	public final SiliconEngine engine;
	
	private NdCodebase codebase;
	
	private Path rootPath;
	
	
	private Map<String, MgRepositoryHandler> map = new ConcurrentHashMap<>();
	
	private JOOS_Lexicon mapLexicon;
	
	
	public MgStore(SiliconEngine engine, MgConfiguration config, Class<?>... classes) throws NdBuildException {
		super();
		this.engine = engine;
		setup(config);
		codebase = NdCodebase.from(classes);
		JOOS_init();
	}
	
	
	public JOOS_Lexicon JOOS_getLexicon() {
		return mapLexicon;
	}
	
	private void JOOS_init() {
		try {
			mapLexicon = JOOS_Lexicon.from(MgRepositoryHandler.class);
		} 
		catch (JOOS_CompilingException e) {
			e.printStackTrace();
		}
	}
	
	
	private void setup(MgConfiguration config) {
		this.rootPath = Path.of(config.rootPath);
	}
	
	
	
	public Path getRootPath() {
		return rootPath;
	}
	

	public SiliconEngine getEngine() {
		return engine;
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
	
}
