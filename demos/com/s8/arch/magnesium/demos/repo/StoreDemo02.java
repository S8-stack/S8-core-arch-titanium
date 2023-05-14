package com.s8.arch.magnesium.demos.repo;

import java.io.IOException;

import com.s8.arch.magnesium.store.MgStore;
import com.s8.arch.magnesium.store.config.ConfigWrapper;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.neodymium.demos.repo2.MyBuilding;
import com.s8.io.bohr.neodymium.exceptions.NdBuildException;
import com.s8.io.xml.handler.type.XML_TypeCompilationException;

public class StoreDemo02 {

	
	/**
	 * 
	 * @param args
	 * @throws XML_TypeCompilationException 
	 * @throws NdBuildException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XML_TypeCompilationException, NdBuildException, IOException {
		ConfigWrapper wrapper = ConfigWrapper.load("config/store-config.xml");
		SiliconEngine engine = new SiliconEngine(wrapper.siConfig);
		engine.start();
		
		MgStore store = new MgStore(engine, wrapper.mgConfig, MyBuilding.class);

		System.out.println("done");
		
	}

}
