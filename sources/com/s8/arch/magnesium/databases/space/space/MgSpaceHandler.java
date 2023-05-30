package com.s8.arch.magnesium.databases.space.space;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.databases.space.store.SpaceMgStore;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.lithium.branches.LiBranch;

public class MgSpaceHandler extends H3MgHandler<LiBranch> {

	
	private final SpaceMgStore store;
	
	private final IOModule ioModule = new IOModule(this);
	
	
	private final String id;
	
	private final Path dataPath;
	
	public MgSpaceHandler(SiliconEngine ng, SpaceMgStore store, String id, Path dataPath) {
		super(ng);
		this.store = store;
		this.id = id;
		this.dataPath = dataPath;
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
		return dataPath;
	}

	public SpaceMgStore getStore() {
		return store;
	}

	public String getIdentifier() {
		return id;
	}
	
	
	
	
	
	

	
	/**
	 * 
	 * @param t
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposure(long t, MgCallback<SpaceExposureS8AsyncOutput> onSucceed, long options) {
		pushOperation(new AccessExposureOp(t, this, onSucceed, options));
	}
	
	
	/**
	 * 
	 * @param t
	 * @param onSucceed
	 * @param onFailed
	 */
	public void exposeObjects(long t, Object[] objects, MgCallback<SpaceVersionS8AsyncOutput> onSucceed, long options) {
		pushOperation(new ExposeObjectsOp(t, this, objects, onSucceed, options));
	}


}
