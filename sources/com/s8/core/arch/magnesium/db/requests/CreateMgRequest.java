package com.s8.core.arch.magnesium.db.requests;

import java.nio.file.Path;

public abstract class CreateMgRequest<R> extends MgRequest<R> {

	public @Override Type getType() { return Type.CREATE; }

	
	
	public final R resource;
	
	public final boolean isOverridingEnabled;
	

	public CreateMgRequest(long t, String key, R resource, boolean isOverridingEnabled) {
		super(t, key);
		this.resource = resource;
		this.isOverridingEnabled = isOverridingEnabled;
	}
	
	
	public abstract void onPathGenerated(Path path);
	
	
	
	public abstract void onEntryCreated(boolean isSucessful);
	
	

}
