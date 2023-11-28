package com.s8.core.arch.magnesium.databases.space.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.outputs.SpaceExposureS8AsyncOutput;
import com.s8.api.flow.outputs.SpaceVersionS8AsyncOutput;
import com.s8.core.arch.magnesium.callbacks.MgCallback;
import com.s8.core.arch.magnesium.databases.space.store.SpaceMgStore;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.lithium.branches.LiBranch;


/**
 * 
 * @author pierreconvert
 *
 */
public class MgSpaceHandler extends H3MgHandler<LiBranch> {

	
	/**
	 * 
	 */
	public final static String DATA_FILENAME = "branch-data.li";
	
	
	/**
	 * 
	 */
	private final SpaceMgStore store;
	
	
	/**
	 * 
	 */
	private final IOModule ioModule = new IOModule(this);
	
	
	/**
	 * 
	 */
	private final String id;
	
	/**
	 * 
	 */
	private final Path folderPath;
	
	
	
	/**
	 * 
	 * @param ng
	 * @param store
	 * @param id
	 * @param folderPath
	 */
	public MgSpaceHandler(SiliconEngine ng, SpaceMgStore store, String id, Path folderPath) {
		super(ng);
		this.store = store;
		this.id = id;
		this.folderPath = folderPath;
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

	public Path getFolderPath() {
		return folderPath;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Path getDataFilePath() {
		return folderPath.resolve(DATA_FILENAME);
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
	public void accessSpace(long t, S8User initiator, MgCallback<SpaceExposureS8AsyncOutput> onSucceed, long options) {
		pushOpLast(new AccessSpaceOp(t, initiator, this, onSucceed, options));
	}
	
	
	/**
	 * 
	 * @param t
	 * @param onSucceed
	 * @param onFailed
	 */
	public void exposeObjects(long t, S8User initiator, Object[] objects, MgCallback<SpaceVersionS8AsyncOutput> onSucceed, long options) {
		pushOpLast(new ExposeObjectsOp(t, initiator, this, objects, onSucceed, options));
	}


}
