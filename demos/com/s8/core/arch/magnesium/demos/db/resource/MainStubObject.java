package com.s8.core.arch.magnesium.demos.db.resource;

import java.util.HashMap;
import java.util.Map;

import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;

@JSON_Type(name = "MainStubObject")
public class MainStubObject {


	@JSON_Field(name = "name") 
	public String name;
	
	@JSON_Field(name = "address") 
	public String address;
	
	@JSON_Field(name = "owner") 
	public String owner;
	
	@JSON_Field(name = "info") 
	public String info;


	@JSON_Field(name = "branches")
	public Map<String, SubStubObject> branches;

	
	/**
	 * 
	 * @return
	 */
	public MainStubObject deepClone() {
		MainStubObject clone = new MainStubObject();
		clone.name = name;
		clone.address = address;
		clone.owner = owner;
		clone.info = info;
		
		HashMap<String, SubStubObject> cloneBranches = new HashMap<>();
		branches.forEach((id, bMetadata) -> cloneBranches.put(id, bMetadata.deepClone()));
		clone.branches = cloneBranches;
		
		return clone;
	}
	
	/**
	 * 
	 * @return
	 */
	public MainStubObject shallowClone() {
		MainStubObject clone = new MainStubObject();
		clone.name = name;
		clone.address = address;
		clone.owner = owner;
		clone.info = info;
		
		return clone;
	}



}
