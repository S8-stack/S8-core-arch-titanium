package com.s8.core.arch.magnesium.demos.db.resource;

import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JSON_Type(name = "MgBranchMetadata")
public class SubStubObject {


	@JSON_Field(name = "id")
	public String name;


	@JSON_Field(name = "info")
	public String info;


	@JSON_Field(name = "headVersion")
	public long headVersion;
	

	@JSON_Field(name = "owner")
	public String owner;

	/*
	@JOOS_Field(name = "commits")
	public List<MgBranchCommitInfo> commits;
	 */



	/**
	 * 
	 * @return
	 */
	public SubStubObject deepClone() {
		SubStubObject clone = new SubStubObject();
		clone.name = name;
		clone.info = info;
		clone.headVersion = headVersion;
		clone.owner = owner;
		return clone;
	}

}
