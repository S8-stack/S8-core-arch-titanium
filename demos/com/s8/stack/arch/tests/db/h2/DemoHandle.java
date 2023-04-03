package com.s8.stack.arch.tests.db.h2;

import com.s8.arch.silicon.SiliconEngine;
import com.s8.blocks.bytes.demos.d0.FatDemoFile02;
import com.s8.stack.arch.magnesium.handles.h2.H2Handle;
import com.s8.stack.arch.magnesium.handles.h2.H2ModelPrototype;
import com.s8.stack.arch.magnesium.handles.h2.H2Operator;

public class DemoHandle extends H2Handle<FatDemoFile02> {

	private final H2TestContext context;

	private H2ModelPrototype<FatDemoFile02> proto;

	public DemoHandle(Props props, H2ModelPrototype<FatDemoFile02> proto, H2TestContext context) {
		super(props);
		this.proto = proto;
		this.context = context;
	}

	@Override
	public SiliconEngine getAppEngine() {
		return context.ng;
	}


	@Override
	public String describe() {
		return "demo-handle";
	}


	@Override
	public void reroute(H2Operator<FatDemoFile02> operator) {
		context.operate(operator);
	}


	@Override
	public void detach() {
		// detach
		context.h = null;
		//System.out.println("Detached!!");
	}

	@Override
	public H2ModelPrototype<FatDemoFile02> getPrototype() {
		return proto;
	}


}
