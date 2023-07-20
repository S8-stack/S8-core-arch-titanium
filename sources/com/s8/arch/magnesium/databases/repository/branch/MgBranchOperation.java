package com.s8.arch.magnesium.databases.repository.branch;

import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.silicon.async.SiAsyncTask;

public abstract class MgBranchOperation {
	
	
	public final MgBranchHandler handler;

	
	
	public final ExceptionMgCallback onFailed;
	
	public MgBranchOperation(MgBranchHandler handler, ExceptionMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onFailed = onFailed;
	}
	
	
	public abstract SiAsyncTask createTask();
	

	
}
