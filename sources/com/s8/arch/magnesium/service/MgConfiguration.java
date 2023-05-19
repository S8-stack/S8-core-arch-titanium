package com.s8.arch.magnesium.service;

import com.s8.io.xml.annotations.XML_SetElement;
import com.s8.io.xml.annotations.XML_Type;


@XML_Type(name = "MagnesiumDbConfiguration")
public class MgConfiguration {
	
	
	
	public String userDbConfigPathname;

	public String spaceDbConfigPathname;

	public String repoDbConfigPathname;

	
	@XML_SetElement(tag = "user-db-config")
	public void setUserDbPathname(String pathname) {
		this.userDbConfigPathname = pathname;
	}
	
	
	@XML_SetElement(tag = "space-db-config")
	public void setSpaceDbPathname(String pathname) {
		this.spaceDbConfigPathname = pathname;
	}
	
	
	@XML_SetElement(tag = "repo-db-config")
	public void setRepoDbPathname(String pathname) {
		this.repoDbConfigPathname = pathname;
	}
	
	
}
