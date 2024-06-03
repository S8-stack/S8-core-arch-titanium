package com.s8.core.arch.magnesium.demos.db;

import java.nio.file.Path;
import java.util.HashMap;

import com.s8.core.arch.magnesium.demos.db.resource.MainStubObject;
import com.s8.core.arch.magnesium.demos.db.resource.SubStubObject;
import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.titanium.db.TiDbSwitcher;
import com.s8.core.arch.titanium.db.requests.CreateTiRequest;
import com.s8.core.io.json.types.JSON_CompilingException;

public class DbTest02 {
	
	
	public static void main(String[] args) throws JSON_CompilingException, InterruptedException {

		SiliconConfiguration siConfiguration = SiliconConfiguration.createDefault4Cores();
		SiliconEngine ng = new SiliconEngine(siConfiguration);
		ng.start();
		
		
		TiDbSwitcher<MainStubObject> db = DbCreator.createDb(ng);
		
		
		/* <metadata> */
		MainStubObject metadata = new MainStubObject();
		metadata.name = "this is my name";
		metadata.address = "wherever the wind blows";
		metadata.info = "No info";
		metadata.owner = "whovere is ready for";
		metadata.branches = new HashMap<>();

		/* define a new (main) branch */
		SubStubObject mainBranchMetadata = new SubStubObject();
		mainBranchMetadata.name = "sub0";
		mainBranchMetadata.info = "Created as MAIN branch";
		mainBranchMetadata.headVersion = 0L;
		mainBranchMetadata.owner = "Wouaou";
		metadata.branches.put("main2", mainBranchMetadata);
		
		
		db.processRequest(new CreateTiRequest<MainStubObject>(0, "asset-18672", metadata, true, false) {

			@Override
			public void onPathGenerated(Path path) {
				System.out.print("path is: "+path);
			}

			@Override
			public void onProcessed(ReturnedStatus status) {
				System.out.print("on-created: "+status);
			}
		});
		
		
		Thread.sleep(1000);

		
		ng.stop();
		
	}

}
