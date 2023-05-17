package com.s8.arch.magnesium.userbase;

import java.nio.file.Path;
import java.util.List;

import com.s8.arch.magnesium.core.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.handler.MgHandler;
import com.s8.arch.magnesium.core.handler.MgIOModule;
import com.s8.arch.magnesium.core.handler.MgUnmountable;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeBuildException;


/**
 * 
 * @author pc
 *
 */
public class MgUserbase extends MgHandler<BeBranch> {

	
	
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
	public MgIOModule<BeBranch> getIOModule() {
		return ioModule;
	}

	@Override
	public void getSubUnmountables(List<MgUnmountable> unmountables) {
		// nothing to map
	}

	public Path getPath() {
		return path;
	}
	
	
	public void login(long t, String username, String password, BooleanMgCallback onProcessed, ExceptionMgCallback onFailed) {
		pushOperation(new LogInOp(t, this, username, password, onProcessed, onFailed));
	}
	
}
