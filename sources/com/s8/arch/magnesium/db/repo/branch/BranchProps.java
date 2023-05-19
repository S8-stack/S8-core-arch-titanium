package com.s8.arch.magnesium.db.repo.branch;

import com.s8.io.joos.JOOS_Field;
import com.s8.io.joos.JOOS_Type;

@JOOS_Type(name = "branch")
public class BranchProps {

	
	@JOOS_Field(name = "id")
	public String id;


	@JOOS_Field(name = "name")
	public String name;

	@JOOS_Field(name = "version")
	public long version;

}
