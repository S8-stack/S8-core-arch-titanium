package com.s8.arch.magnesium.repository;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.handler.MgIOModule;
import com.s8.arch.magnesium.handler.MgHandler;
import com.s8.arch.magnesium.handler.MgUnmountable;
import com.s8.arch.magnesium.store.MgStore;

/**
 * 
 * @author pierreconvert
 *
 */
public class MgRepositoryHandler extends MgHandler<MgRepository> {
	
	
	public MgStore store;
	
	public String id;
	
	public Path path;
	
	
	public MgRepository repository;
	
	
	private final IOModule ioModule = new IOModule(this);
	
	
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
	public MgIOModule<MgRepository> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<MgUnmountable> unmountables) {
		MgRepository repository = getResource();
		if(repository != null) { repository.crawl(unmountables); }
	}

}
