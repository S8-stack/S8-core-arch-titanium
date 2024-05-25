package com.s8.core.arch.magnesium.db.requests;

public abstract class DeleteMgRequest<R> extends MgRequest<R> {

	public @Override Type getType() { return Type.DELETE; }

	

	public DeleteMgRequest(long t, String key) {
		super(t, key);
	}
	
	
	
	public abstract void onDelete(boolean isSucessful);
	
	

}
