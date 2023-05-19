package com.s8.arch.magnesium.db.repo.branch;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.silicon.async.AsyncTask;

public abstract class MgBranchOperation {
	
	
	public final MgBranchHandler handler;

	
	
	public final ExceptionMgCallback onFailed;
	
	public MgBranchOperation(MgBranchHandler handler, ExceptionMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onFailed = onFailed;
	}
	
	
	public abstract AsyncTask createTask();
	

	
}
