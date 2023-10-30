package com.s8.arch.magnesium.databases.repository.store;

import com.s8.core.io.joos.JOOS_Field;
import com.s8.core.io.joos.JOOS_Type;



@JOOS_Type(name = "RepositoryStore")
public class RepoMgStoreMetadata {


	@JOOS_Field(name = "rootPathname") 
	public String rootPathname;


}
