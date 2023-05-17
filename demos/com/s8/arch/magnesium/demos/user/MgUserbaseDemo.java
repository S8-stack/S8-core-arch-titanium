package com.s8.arch.magnesium.demos.user;

import java.nio.file.Path;

import com.s8.arch.magnesium.userbase.MgUserbase;
import com.s8.arch.silicon.SiliconConfiguration;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.beryllium.exception.BeBuildException;

public class MgUserbaseDemo {

	public static void main(String[] args) throws BeBuildException {

		SiliconConfiguration configuration = new SiliconConfiguration();
		SiliconEngine ng = new SiliconEngine(configuration);
		ng.start();
		
		Path path = Path.of("data/userbase/userbase.be");
		MgUserbase userbase = new MgUserbase(ng, path);
		
		for(int i = 0; i<1000; i++) {
			userbase.login(0, "convert.pierre@gmail.com", "toto1234", f -> System.out.println("Is logged-in: "+f), e -> e.printStackTrace());	
		}
		
		
	}

}
