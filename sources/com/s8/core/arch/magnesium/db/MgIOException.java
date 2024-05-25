package com.s8.core.arch.magnesium.db;

import java.io.IOException;

public class MgIOException extends IOException {

	
	
	private static final long serialVersionUID = 1859966423603716386L;
	
	
	public final MgResourceStatus status;
	

	public MgIOException(MgResourceStatus status) {
		super();
		this.status = status;
	}
		
}
