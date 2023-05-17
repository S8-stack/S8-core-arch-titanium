/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.arch.magnesium {
	
	

	exports com.s8.arch.magnesium.core.handler;
	exports com.s8.arch.magnesium.core.callbacks;
	
	exports com.s8.arch.magnesium.repobase.branch;
	exports com.s8.arch.magnesium.repobase.repository;
	exports com.s8.arch.magnesium.repobase.store;
	
	exports com.s8.arch.magnesium.userbase;

	exports com.s8.arch.magnesium.store.config;
	
	
	exports com.s8.arch.magnesium.oldcallbacks;
	exports com.s8.arch.magnesium.handles.h1;
	exports com.s8.arch.magnesium.handles.h2;
	
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
	
}