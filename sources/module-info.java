/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.arch.magnesium {
	
	

	exports com.s8.arch.magnesium.handlers.h1;
	exports com.s8.arch.magnesium.handlers.h2;
	exports com.s8.arch.magnesium.handlers.h3;
	
	
	exports com.s8.arch.magnesium.callbacks;
	

	exports com.s8.arch.magnesium.databases.note;

	exports com.s8.arch.magnesium.databases.space.space;
	exports com.s8.arch.magnesium.databases.space.store;
	
	exports com.s8.arch.magnesium.databases.repo.branch;
	exports com.s8.arch.magnesium.databases.repo.repository;
	exports com.s8.arch.magnesium.databases.repo.store;
	

	exports com.s8.arch.magnesium.service;
	
	
	exports com.s8.arch.magnesium.oldcallbacks;
	
	exports com.s8.arch.magnesium.stores.m1;
	exports com.s8.arch.magnesium.stores.m2;
	exports com.s8.arch.magnesium.stores.m3;
	exports com.s8.arch.magnesium.stores.m3.nodes;
	exports com.s8.arch.magnesium.stores.m3.requests;
	
	exports com.s8.arch.magnesium.stores.m4;
	exports com.s8.arch.magnesium.core.paths;
	
	
	
	
	
	
	
	
	requires transitive com.s8.io.xml;
	requires transitive com.s8.io.bytes;
	requires transitive com.s8.io.bohr.neodymium;
	
	requires transitive com.s8.arch.silicon;
	requires transitive com.s8.io.joos;
	requires transitive com.s8.io.bohr.beryllium;
	requires transitive com.s8.io.bohr.lithium;
	requires com.s8.arch.fluor;
	
}