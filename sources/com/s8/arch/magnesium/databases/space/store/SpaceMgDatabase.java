package com.s8.arch.magnesium.databases.space.store;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.lithium.codebase.LiCodebase;


/**
 * 
 * @author pc
 *
 */
public class SpaceMgDatabase extends H3MgHandler<SpaceMgStore> {

	
	public final LiCodebase codebase;
	
	public final Path storeInfoPathname;
	
	public final IOModule ioModule = new IOModule(this);
	
	public SpaceMgDatabase(SiliconEngine ng, LiCodebase codebase, Path storeInfoPathname) {
		super(ng);
		this.codebase = codebase;
		this.storeInfoPathname = storeInfoPathname;
	}

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public H3MgIOModule<SpaceMgStore> getIOModule() {
		return ioModule;
	}

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		SpaceMgStore store = getResource();
		if(store != null) { 
			return store.getSpaceHandlers(); 
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
	 * @param t
	 * @param spaceId
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposure(long t, String spaceId, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposureOp(t, this, spaceId, onSucceed, onFailed));
	}

	
	

	/**
	 * 
	 * @param t
	 * @param spaceId
	 * @param onSucceed
	 * @param onFailed
	 */
	public void exposeObjects(long t, String spaceId, Object[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new ExposeObjectsOp(t, this, spaceId, objects, onSucceed, onFailed));
	}

	
	
}
