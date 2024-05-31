package com.s8.core.arch.magnesium.demos.db;

import java.nio.file.Path;

import com.s8.core.arch.magnesium.demos.db.resource.MainStubObject;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.titanium.db.TiDbSwitcher;
import com.s8.core.arch.titanium.db.TiPathComposer;
import com.s8.core.io.json.types.JSON_CompilingException;

public class DbCreator {

	
	public final static String ROOT_PATHNAME = "data/db";
	
	
	/**
	 * 
	 * @param ng
	 * @return
	 * @throws JSON_CompilingException
	 */
	public static TiDbSwitcher<MainStubObject> createDb(SiliconEngine ng) throws JSON_CompilingException {
		
		
		TiPathComposer composer = new TiPathComposer() {
			
			@Override
			public Path composePath(String key) {
				return Path.of(ROOT_PATHNAME).resolve(key);
			}
		};
		
		IOModule module = new IOModule();
		
		return new TiDbSwitcher<>(ng, composer) {
			public IOModule getIOModule() { return module; }
		};
	}
}
