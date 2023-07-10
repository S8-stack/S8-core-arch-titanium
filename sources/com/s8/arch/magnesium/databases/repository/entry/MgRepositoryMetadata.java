package com.s8.arch.magnesium.databases.repository.entry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.s8.arch.fluor.metadatas.S8BranchMetadata;
import com.s8.arch.fluor.metadatas.S8RepositoryMetadata;
import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

@JOOS_Type(name = "mg-repository")
public class MgRepositoryMetadata implements S8RepositoryMetadata {


	@JOOS_Field(name = "name") 
	public String name;
	
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
		clone.name = name;
		clone.address = address;
		clone.creationDate = creationDate;
		clone.owner = owner;
		clone.info = info;
		
		HashMap<String, MgBranchMetadata> cloneBranches = new HashMap<>();
		branches.forEach((id, bMetadata) -> cloneBranches.put(id, bMetadata.deepClone()));
		clone.branches = cloneBranches;
		
		return clone;
	}
	
	/**
	 * 
	 * @return
	 */
	public MgRepositoryMetadata shallowClone() {
		MgRepositoryMetadata clone = new MgRepositoryMetadata();
		clone.name = name;
		clone.address = address;
		clone.creationDate = creationDate;
		clone.owner = owner;
		clone.info = info;
		
		return clone;
	}



	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public String getAddress() {
		return address;
	}


	@Override
	public long getCreationDate() {
		return creationDate;
	}


	@Override
	public String getOwner() {
		return owner;
	}


	@Override
	public String getInfo() {
		return info;
	}


	@Override
	public int getNbBranches() {
		return branches.size();
	}

	@Override
	public void crawlBranches(BiConsumer<String, S8BranchMetadata> consumer) {
		branches.forEach(consumer);
	}	
}
