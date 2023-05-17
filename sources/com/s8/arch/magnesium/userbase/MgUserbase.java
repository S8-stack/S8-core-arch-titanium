package com.s8.arch.magnesium.userbase;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.core.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.magnesium.handlers.h3.H3MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeBuildException;


/**
 * 
 * @author pc
 *
 */
public class MgUserbase extends H3MgHandler<BeBranch> {

	
	
	public final BeCodebase codebase;
	
	public final Path path;
	
	private final IOModule ioModule = new IOModule(this);
	
	public MgUserbase(SiliconEngine ng, Path path) throws BeBuildException {
		super(ng);
		
		this.codebase = BeCodebase.from(MgUser.class);
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
	
	
	public void login(long t, String username, String password, BooleanMgCallback onProcessed, ExceptionMgCallback onFailed) {
		pushOperation(new LogInOp(t, this, username, password, onProcessed, onFailed));
	}
	
}
