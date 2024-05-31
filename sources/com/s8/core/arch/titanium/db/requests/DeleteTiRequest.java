package com.s8.core.arch.titanium.db.requests;

public abstract class DeleteTiRequest<R> extends TiRequest<R> {

	public @Override Type getType() { return Type.DELETE; }

	

	public DeleteTiRequest(long t, String key) {
		super(t, key);
	}
	
	
	
	public abstract void onDelete(boolean isSucessful);
	
	

}
