package com.s8.arch.magnesium.databases.user;

import com.s8.io.bohr.atom.annotations.S8Field;
import com.s8.io.bohr.atom.annotations.S8ObjectType;
import com.s8.io.bohr.beryllium.object.BeObject;


@S8ObjectType(name = "base-s8-user")
public class MgUser extends BeObject {
	
	
	@S8Field(name = "display-name")
	public String displayName;
	
	@S8Field(name = "password")
	public String password;
	
	@S8Field(name = "workspace")
	public String workspace;
	
	
	/**
	 * User id = email address
	 * 
	 * @param id
	 */
	public MgUser(String id) {
		super(id);
	}
	
	
	public String getUsername() {
		return S8_key;
	}
	
	
	public String getPassword() {
		return password;
	}
}
