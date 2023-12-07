package com.s8.core.arch.magnesium.databases.space.store;

import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;

@JSON_Type(name = "SpaceMgStoreMetadata")
public class SpaceMgStoreMetadata {


	@JSON_Field(name = "rootFolderPathname") 
	public String rootFolderPathname;


}
