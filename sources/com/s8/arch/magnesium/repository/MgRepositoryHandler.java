package com.s8.arch.magnesium.repository;

import java.nio.file.Path;

import com.s8.arch.magnesium.shared.LoadMgTask;
import com.s8.arch.magnesium.shared.MgSharedResourceHandler;
import com.s8.arch.magnesium.shared.SaveMgTask;
import com.s8.arch.magnesium.store.MgStore;
import com.s8.io.joos.JOOS_Lexicon;
import com.s8.io.joos.types.JOOS_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends MgSharedResourceHandler<MgRepository> {
	

	private static JOOS_Lexicon lexicon;
	
	public static JOOS_Lexicon JOOS_getLexicon() throws JOOS_CompilingException {
		if(lexicon == null) { lexicon = JOOS_Lexicon.from(MgRepository.class); }
		return lexicon;
	}
	
	public MgStore store;
	
	public String id;
	
	public Path path;
	
	
	public MgRepository repository;
	
	
	public MgRepositoryHandler(Path path) {
		super();
		this.path = path;
	}

	
	

	public Path getPath() {
		return path;
	}
	

	


	/**
	 * 
	 * @return
	 */
	public MgStore getStore() {
		return store;
	}


	@Override
	public String getName() {
		return id;
	}


	@Override
	public LoadMgTask<MgRepository> createLoadTask() {
		return new Load(this);
	}


	@Override
	public SaveMgTask<MgRepository> createSaveTask(MgRepository repo) {
		return new Save(this, repo);
	}
	
}
