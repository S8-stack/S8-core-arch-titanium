package com.s8.arch.magnesium.databases.space.store;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.S8User;
import com.s8.arch.fluor.outputs.SpaceExposureS8AsyncOutput;
import com.s8.arch.fluor.outputs.SpaceVersionS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.joos.types.JOOS_CompilingException;


/**
 * 
 * @author pc
 *
 */
public class SpaceMgDatabase extends H3MgHandler<SpaceMgStore> {

	
	public final LiCodebase codebase;
	
	public final Path storeInfoPathname;
	
	public final IOModule ioModule;
	
	public final MgSpaceInitializer initializer;
	
	/**
	 * 
	 * @param ng
	 * @param codebase
	 * @param storeInfoPathname
	 * @param initializer
	 * @throws JOOS_CompilingException
	 */
	public SpaceMgDatabase(SiliconEngine ng, 
			LiCodebase codebase, 
			Path storeInfoPathname, 
			MgSpaceInitializer initializer) throws JOOS_CompilingException {
		super(ng);
		this.codebase = codebase;
		this.storeInfoPathname = storeInfoPathname;
		this.ioModule = new IOModule(this);
		this.initializer = initializer;
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
	 * @param onProceed
	 * @param onFailed
	 */
	public void accessExposure(long t, S8User initiator, String spaceId, MgCallback<SpaceExposureS8AsyncOutput> onProceed, long options) {
		pushOperation(new AccessExposureOp(t, initiator, this, spaceId, onProceed, options));
	}

	

	/**
	 * 
	 * @param t
	 * @param spaceId
	 * @param onProceed
	 * @param onFailed
	 */
	public void exposeObjects(long t, S8User initiator, String spaceId, Object[] objects, MgCallback<SpaceVersionS8AsyncOutput> onProceed, long options) {
		pushOperation(new ExposeObjectsOp(t, initiator, this, spaceId, objects, onProceed, options));
	}

	
	
}
