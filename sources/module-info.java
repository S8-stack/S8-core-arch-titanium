/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.core.arch.magnesium {
	
	

	exports com.s8.core.arch.magnesium.handlers.h1;
	exports com.s8.core.arch.magnesium.handlers.h2;
	exports com.s8.core.arch.magnesium.handlers.h3;
	
	
	exports com.s8.core.arch.magnesium.callbacks;
	

	exports com.s8.core.arch.magnesium.databases.record;

	exports com.s8.core.arch.magnesium.databases.space.entry;
	exports com.s8.core.arch.magnesium.databases.space.store;
	
	exports com.s8.core.arch.magnesium.databases.repository.branch;
	exports com.s8.core.arch.magnesium.databases.repository.entry;
	exports com.s8.core.arch.magnesium.databases.repository.store;
	

	exports com.s8.core.arch.magnesium.service;
	
	exports com.s8.core.arch.magnesium.core.paths;
	
	
	
	
	
	
	requires transitive com.s8.api;
	
	requires transitive com.s8.core.io.xml;
	requires transitive com.s8.core.io.bytes;
	
	requires transitive com.s8.core.arch.silicon;
	requires transitive com.s8.core.io.joos;

	requires transitive com.s8.core.bohr.neodymium;
	requires transitive com.s8.core.bohr.beryllium;
	requires transitive com.s8.core.bohr.lithium;
	
	
}