package com.s8.stack.arch.tests.db.mappings.m1;

public final class BenchValue {

	public final String address;
	
	public final String defaultValue;
	
	public final int ownerThreadIndex;
	
	public BenchValue(String address, String defaultValue, int ownerThreadIndex) {
		super();
		this.address = address;
		this.defaultValue = defaultValue;
		this.ownerThreadIndex = ownerThreadIndex;
	}
	
}
