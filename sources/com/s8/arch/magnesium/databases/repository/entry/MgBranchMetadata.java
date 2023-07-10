package com.s8.arch.magnesium.databases.repository.entry;

import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JOOS_Type(name = "mg-branch-info")
public class MgBranchMetadata {


	@JOOS_Field(name = "id")
	public String name;


	@JOOS_Field(name = "info")
	public String info;


	@JOOS_Field(name = "head-version")
	public long headVersion;


	@JOOS_Field(name = "fork-id")
	public String forkedBranchId;


	@JOOS_Field(name = "fork-version")
	public long forkedBranchVersion;


	@JOOS_Field(name = "owner")
	public String owner;

	/*
	@JOOS_Field(name = "commits")
	public List<MgBranchCommitInfo> commits;
	 */



	/**
	 * 
	 * @return
	 */
	public MgBranchMetadata deepClone() {
		MgBranchMetadata clone = new MgBranchMetadata();
		clone.name = name;
		clone.info = info;
		clone.headVersion = headVersion;
		clone.forkedBranchId = forkedBranchId;
		clone.forkedBranchVersion = forkedBranchVersion;
		clone.owner = owner;
		return clone;
	}


}
