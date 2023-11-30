package com.s8.arch.magnesium.demos.user;

import java.nio.file.Path;

import com.s8.api.flow.record.objects.RecordS8Object;
import com.s8.api.flow.record.requests.GetRecordS8Request;
import com.s8.core.arch.magnesium.databases.record.RecordsMgDatabase;
import com.s8.core.arch.silicon.SiliconConfiguration;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.beryllium.codebase.BeCodebase;
import com.s8.core.bohr.beryllium.exception.BeBuildException;

public class MgUserbaseDemo {

	public static void main(String[] args) throws BeBuildException {

		SiliconConfiguration configuration = new SiliconConfiguration();
		SiliconEngine ng = new SiliconEngine(configuration);
		ng.start();

		Path path = Path.of("data/userbase/userbase.be");
		RecordsMgDatabase userbase = new RecordsMgDatabase(ng, BeCodebase.from(MgUser.class), path);

		for(int i = 0; i<1; i++) {
			userbase.get(0, () -> {}, new GetRecordS8Request("convert.pierre@gmail.com") {
				
				@Override
				public void onSucceed(Status status, RecordS8Object record) {
					MgUser user = (MgUser) record;
					System.out.println("Is logged-in: "+user.password.equals("toto1234"));	
				}
				
				@Override
				public void onFailed(Exception exception) {
					exception.printStackTrace();
				}
			});	
		}


	}

}
