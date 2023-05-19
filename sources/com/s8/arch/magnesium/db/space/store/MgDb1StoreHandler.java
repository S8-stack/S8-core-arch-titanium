package com.s8.arch.magnesium.db.space.store;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.magnesium.handlers.h3.H3MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.lithium.codebase.LiCodebase;


/**
 * 
 * @author pc
 *
 */
public class MgDb1StoreHandler extends H3MgHandler<MgS1Store> {

	
	public final LiCodebase codebase;
	
	public final Path storeInfoPathname;
	
	public final IOModule ioModule = new IOModule(this);
	
	public MgDb1StoreHandler(SiliconEngine ng, LiCodebase codebase, Path storeInfoPathname) {
		super(ng);
		this.codebase = codebase;
		this.storeInfoPathname = storeInfoPathname;
	}

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public H3MgIOModule<MgS1Store> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<H3MgUnmountable> unmountables) {
		MgS1Store store = getResource();
		if(store != null) { store.crawl(unmountables); }
	}

	public Path getInfoPath() {
		return storeInfoPathname;
	}

	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposed(long t, String repoAddress,int slot, ObjectMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposedOp(t, this, repoAddress, slot, onSucceed, onFailed));
	}
	
	
	/**
	 * 
	 * @param t
	 * @param repoAddress
	 * @param onSucceed
	 * @param onFailed
	 */
	public void accessExposure(long t, String repoAddress, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		pushOperation(new AccessExposureOp(t, this, repoAddress, onSucceed, onFailed));
	}

	
	
}
