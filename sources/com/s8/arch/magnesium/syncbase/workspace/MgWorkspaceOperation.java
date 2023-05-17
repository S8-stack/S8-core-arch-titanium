package com.s8.arch.magnesium.syncbase.workspace;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.silicon.async.AsyncTask;

public abstract class MgWorkspaceOperation {
	
	
	public final MgWorkspaceHandler handler;

	
	
	public final ExceptionMgCallback onFailed;
	
	public MgWorkspaceOperation(MgWorkspaceHandler handler, ExceptionMgCallback onFailed) {
		super();
		this.handler = handler;
		this.onFailed = onFailed;
	}
	
	
	public abstract AsyncTask createTask();
	

	
}
