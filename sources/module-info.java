/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.core.arch.titanium {
	
	

	exports com.s8.core.arch.titanium.db;
	exports com.s8.core.arch.titanium.db.io;
	exports com.s8.core.arch.titanium.db.requests;
	
	
	exports com.s8.core.arch.titanium.handlers.h1;
	exports com.s8.core.arch.titanium.handlers.h2;
	exports com.s8.core.arch.titanium.handlers.h3;
	
	
	exports com.s8.core.arch.titanium.callbacks;
	


	exports com.s8.core.arch.titanium.databases;
	

	exports com.s8.core.arch.titanium.service;
	
	exports com.s8.core.arch.titanium.core.paths;
	
	
	
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