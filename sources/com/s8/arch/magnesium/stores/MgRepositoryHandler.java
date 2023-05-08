package com.s8.arch.magnesium.stores;

import java.nio.file.Path;
import java.util.Map;

import com.s8.arch.magnesium.branches.MgBranchHandler;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JOOS_Type(name = "repository")
public class MgRepositoryHandler {
	
	public String id;
	
	public Path path;
	
	@JOOS_Field(name = "name")
	public String name;
	
	@JOOS_Field(name = "branches")
	public Map<String, MgBranchHandler> branchHandlers;
	
	
	public MgRepositoryHandler() {
		super();
	}
	
	public void link() {
		branchHandlers.forEach((k, v) -> v.link(this));
	}
	
	/**
	 * 
	 */
	public void initialize() {
		
		MgBranchHandler primeBranchHandler = new MgBranchHandler();
		primeBranchHandler.name = "prime";
		primeBranchHandler.version = 0x00L;
		
		branchHandlers.put("prime", primeBranchHandler);
	}
	
	

	
	
}
