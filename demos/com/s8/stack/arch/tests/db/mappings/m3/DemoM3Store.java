package com.s8.stack.arch.tests.db.mappings.m3;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.stack.arch.magnesium.stores.m3.M3ModelPrototype;
import com.s8.stack.arch.magnesium.stores.m3.M3Store;

public class DemoM3Store extends M3Store<String> {

	
	private M3ModelPrototype<String> proto;
	
	public DemoM3Store(Path root, M3ModelPrototype<String> proto) {
		super(root);
		this.proto = proto;
	}

	@Override
	public M3ModelPrototype<String> getPrototype() {
		return proto;
	}

	@Override
	public void serialize(ByteOutflow outflow) throws IOException {
		serializeStore(outflow);
	}

	@Override
	public void deserialize(ByteInflow inflow) throws IOException {
		deserializeStore(inflow);
	}
	
}
