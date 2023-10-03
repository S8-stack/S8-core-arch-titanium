package com.s8.arch.magnesium.demos.user;

import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.api.objects.table.TableS8Object;


@S8ObjectType(name = "mg-user")
public class MgUser extends TableS8Object {

	
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
