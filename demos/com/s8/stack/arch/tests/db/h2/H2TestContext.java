package com.s8.stack.arch.tests.db.h2;

import java.nio.file.Paths;

import com.s8.arch.silicon.SiliconConfiguration;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.blocks.bytes.demos.d0.FatDemoFile02;
import com.s8.stack.arch.magnesium.handles.h2.H2Handle;
import com.s8.stack.arch.magnesium.handles.h2.H2Handle.Props;
import com.s8.stack.arch.magnesium.handles.h2.H2Operator;


/**
 * 
 * @author pierreconvert
 *
 */
public class H2TestContext {


	public final SiliconEngine ng;

	private final H2DemoPrototype prototype;

	private final FatDemoFile02 file;

	public final long checkSum;

	public volatile DemoHandle h;


	public H2TestContext() {
		super();
		// create engine
		SiliconConfiguration config = new SiliconConfiguration();
		ng = new SiliconEngine(config);
		ng.start();

		prototype = new H2DemoPrototype(Paths.get("data/test-file"));


		file = new FatDemoFile02();
		file.generateData(197987);
		checkSum = file.checkSum;
		
		// initialize
		DemoHandle handle = getHandle();
		handle.initializeReady(file);
		handle.requestShutDown(null);
	}
	
	

	/**
	 * 
	 */
	public void run(int nBots, int nOps) {
		for(int i=0; i<nBots; i++) {
			new H2TestBot("bot("+i+")", i, this, nOps).start();
		}
		new AutoCloseBot("auto-close", 0, this, nOps).start();
	}

	


	public void operate(H2Operator<FatDemoFile02> operator) {
		getHandle().operate(operator);
	}




	public synchronized DemoHandle getHandle() {
		if(h==null) {
			H2Handle.Props props = new Props();
			props.buffering = 1024;
			props.isVerbose = true;
			h = new DemoHandle(props, prototype, this);
		}
		return h;
	}
}
