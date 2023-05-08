package com.s8.arch.magnesium.branches;

import com.s8.arch.silicon.async.AsyncTask;

public abstract class MgBranchOperation {
	
	
	public final MgBranchHandler handler;

	
	
	public final ErrorMgCallback onFailed;
	
	public MgBranchOperation(MgBranchHandler handler, ErrorMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onFailed = onFailed;
	}
	
	
	public abstract AsyncTask createTask();
	

	
}
