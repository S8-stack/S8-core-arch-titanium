package com.s8.arch.magnesium.databases.repository.branch;

import com.s8.core.io.joos.JOOS_Field;
import com.s8.core.io.joos.JOOS_Type;

@JOOS_Type(name = "mg-branch-commit-info")
public class MgBranchCommitInfo {
	
	@JOOS_Field(name = "user")
	public String userId;

	@JOOS_Field(name = "date")
	public long timestamp;

	@JOOS_Field(name = "comment")
	public String comment;

}
