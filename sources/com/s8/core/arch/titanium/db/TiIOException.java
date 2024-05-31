package com.s8.core.arch.titanium.db;

import java.io.IOException;

public class TiIOException extends IOException {

	
	
	private static final long serialVersionUID = 1859966423603716386L;
	
	
	public final TiResourceStatus status;
	

	public TiIOException(TiResourceStatus status) {
		super();
		this.status = status;
	}
		
}
