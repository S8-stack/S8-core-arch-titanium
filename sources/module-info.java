/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.core.arch.magnesium {
	
	

	exports com.s8.core.arch.magnesium.db;
	exports com.s8.core.arch.magnesium.db.requests;
	
	
	exports com.s8.core.arch.magnesium.handlers.h1;
	exports com.s8.core.arch.magnesium.handlers.h2;
	exports com.s8.core.arch.magnesium.handlers.h3;
	
	
	exports com.s8.core.arch.magnesium.callbacks;
	


	exports com.s8.core.arch.magnesium.databases;
	

	exports com.s8.core.arch.magnesium.service;
	
	exports com.s8.core.arch.magnesium.core.paths;
	
	
	
	opens com.s8.core.arch.magnesium.demos.db.resource;
	
	
	
	
	
	
	requires transitive com.s8.api;
	
	requires transitive com.s8.core.io.xml;
	requires transitive com.s8.core.io.bytes;
	
	requires transitive com.s8.core.arch.silicon;
	requires transitive com.s8.core.io.json;

	requires transitive com.s8.core.bohr.neodymium;
	requires transitive com.s8.core.bohr.beryllium;
	requires transitive com.s8.core.bohr.lithium;
	
	
}