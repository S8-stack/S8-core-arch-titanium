package com.s8.arch.magnesium.demos.user;

import com.s8.io.bohr.atom.annotations.S8Field;
import com.s8.io.bohr.atom.annotations.S8ObjectType;
import com.s8.io.bohr.beryllium.object.BeObject;


@S8ObjectType(name = "mg-user")
public class MgUser extends BeObject {

	
	@S8Field(name = "username")
	public String displayName;
	
	
	@S8Field(name = "password")
	public String password = "toto1234";
	
	
	@S8Field(name = "workspace")
	public String workspace;
	
	public MgUser(String id) {
		super(id);
	}

}
