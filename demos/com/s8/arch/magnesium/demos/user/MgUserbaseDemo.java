package com.s8.arch.magnesium.demos.user;

import java.nio.file.Path;

import com.s8.arch.magnesium.databases.user.MgUser;
import com.s8.arch.magnesium.databases.user.UserMgDatabase;
import com.s8.arch.silicon.SiliconConfiguration;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeBuildException;

public class MgUserbaseDemo {

	public static void main(String[] args) throws BeBuildException {

		SiliconConfiguration configuration = new SiliconConfiguration();
		SiliconEngine ng = new SiliconEngine(configuration);
		ng.start();

		Path path = Path.of("data/userbase/userbase.be");
		UserMgDatabase userbase = new UserMgDatabase(ng, BeCodebase.from(MgUser.class), path);

		for(int i = 0; i<1; i++) {
			userbase.get(0, "convert.pierre@gmail.com", object -> {
				MgUser user = (MgUser) object;
				System.out.println("Is logged-in: "+user.password.equals("toto1234"));
			}, e -> e.printStackTrace());	
		}


	}

}
