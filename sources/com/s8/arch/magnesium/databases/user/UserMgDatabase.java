package com.s8.arch.magnesium.databases.user;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.ObjectMgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.magnesium.handlers.h3.H3MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.object.BeObject;


/**
 * 
 * @author pc
 *
 */
public class UserMgDatabase extends H3MgHandler<BeBranch> {

	
	
	public final BeCodebase codebase;
	
	public final Path path;
	
	private final IOModule ioModule = new IOModule(this);
	
	public UserMgDatabase(SiliconEngine ng, BeCodebase codebase, Path path) {
		super(ng);
		
		this.codebase = codebase;
		this.path = path;
	}

	
	public BeCodebase getCodebase(){
		return codebase;
	}
	
	@Override
	public String getName() {
		return "USERBASE";
	}

	@Override
	public H3MgIOModule<BeBranch> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<H3MgUnmountable> unmountables) {
		// nothing to map
	}

	public Path getPath() {
		return path;
	}
	
	
	public void get(long t, String key, ObjectMgCallback onRetrieved, ExceptionMgCallback onFailed) {
		pushOperation(new GetOp(t, this, key, onRetrieved, onFailed));
	}
	
	public void put(long t, String key, BeObject object, BooleanMgCallback onInserted, ExceptionMgCallback onFailed) {
		pushOperation(new PutOp(t, this, key, object, onInserted, onFailed));
	}
	
}
