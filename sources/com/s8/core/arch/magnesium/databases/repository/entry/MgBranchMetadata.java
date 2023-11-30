package com.s8.core.arch.magnesium.databases.repository.entry;

import com.s8.api.flow.repository.objects.S8BranchMetadata;
import com.s8.core.io.joos.JOOS_Field;
import com.s8.core.io.joos.JOOS_Type;

/**
 * 
 * @author pierreconvert
 *
 */
@JOOS_Type(name = "MgBranchMetadata")
public class MgBranchMetadata implements S8BranchMetadata {


	@JOOS_Field(name = "id")
	public String name;


	@JOOS_Field(name = "info")
	public String info;


	@JOOS_Field(name = "headVersion")
	public long headVersion;


	@JOOS_Field(name = "forkId")
	public String forkedBranchId;


	@JOOS_Field(name = "forkVersion")
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public long getHeadVersion() {
		return headVersion;
	}

	@Override
	public String getForkedBranchId() {
		return forkedBranchId;
	}

	@Override
	public long getForkedBranchVersion() {
		return forkedBranchVersion;
	}

	@Override
	public String getOwner() {
		return owner;
	}


}
