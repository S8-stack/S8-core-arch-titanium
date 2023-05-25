package com.s8.arch.magnesium.databases.space.space;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.callbacks.VersionMgCallback;
import com.s8.arch.magnesium.databases.space.store.SpaceMgStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.lithium.branches.LiBranch;

public class MgSpaceHandler extends H3MgHandler<LiBranch> {

	
	private final SpaceMgStore store;
	
	private final IOModule ioModule = new IOModule(this);
	
	
	private final String id;
	
	private final Path path;
	
	public MgSpaceHandler(SiliconEngine ng, SpaceMgStore store, String id, Path path) {
		super(ng);
		this.store = store;
		this.id = id;
		this.path = path;
	}

	@Override
	public String getName() {
		return "workspace hanlder";
	}

	@Override
	public H3MgIOModule<LiBranch> getIOModule() {
		return ioModule;
	}

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		return new ArrayList<>(); // no subhandler
	}

	public Path getPath() {
		return path;
	}

	public SpaceMgStore getStore() {
		return store;
	}

	public String getIdentifier() {
		return id;
	}
	
	

	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposed(long t, int slot, ObjectMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposedOp(t, this, slot, onSucceed, onFailed));
	}
	
	
	/**
	 * 
	 * @param t
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposure(long t, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposureOp(t, this, onSucceed, onFailed));
	}
	
	
	/**
	 * 
	 * @param t
	 * @param onSucceed
	 * @param onFailed
	 */
	public void exposeObjects(long t, Object[] objects, VersionMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new ExposeObjectsOp(t, this, objects, onSucceed, onFailed));
	}


}
