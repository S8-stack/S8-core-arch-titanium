package com.s8.arch.magnesium.databases.repository.entry;

import java.util.HashMap;
import java.util.Map;

import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

@JOOS_Type(name = "mg-repository")
public class MgRepositoryMetadata {


	@JOOS_Field(name = "address") 
	public String address;
	
	@JOOS_Field(name = "creation-date") 
	public long creationDate;
	
	@JOOS_Field(name = "owner") 
	public String owner;
	
	@JOOS_Field(name = "info") 
	public String info;


	@JOOS_Field(name = "branches")
	public Map<String, MgBranchMetadata> branches;

	
	/**
	 * 
	 * @return
	 */
	public MgRepositoryMetadata deepClone() {
		MgRepositoryMetadata clone = new MgRepositoryMetadata();
		clone.address = address;
		clone.creationDate = creationDate;
		clone.owner = owner;
		clone.info = info;
		
		HashMap<String, MgBranchMetadata> cloneBranches = new HashMap<>();
		branches.forEach((id, bMetadata) -> cloneBranches.put(id, bMetadata.deepClone()));
		clone.branches = cloneBranches;
		
		return clone;
	}

	
}
