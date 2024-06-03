package com.s8.core.arch.magnesium.demos.db;

import java.nio.file.Path;

import com.s8.core.arch.magnesium.demos.db.resource.MainStubObject;
import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.TiDbSwitcher;
import com.s8.core.arch.titanium.db.requests.AccessTiRequest;
import com.s8.core.io.json.types.JSON_CompilingException;

public class DbTest03 {
	
	
	public static void main(String[] args) throws JSON_CompilingException, InterruptedException {

		SiliconConfiguration siConfiguration = SiliconConfiguration.createDefault4Cores();
		SiliconEngine ng = new SiliconEngine(siConfiguration);
		ng.start();
		
		
		TiDbSwitcher<MainStubObject> db = DbCreator.createDb(ng);
		
		db.processRequest(new AccessTiRequest<MainStubObject>(0, "asset-18672", true) {

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public String describe() {
				return "test-access";
			}

			@Override
			public boolean onProcessed(Path path, ResponseStatus status, MainStubObject resource) {
				System.out.println(resource.address);
				resource.address = "hobbitland";
				return true;
			}
		});
		
		Thread.sleep(1000);

		
		ng.stop();
		
	}

}
