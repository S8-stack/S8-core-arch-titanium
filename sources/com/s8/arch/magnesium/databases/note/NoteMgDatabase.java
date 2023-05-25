package com.s8.arch.magnesium.databases.note;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.arch.fluor.S8Filter;
import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.callbacks.ObjectMgCallback;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.object.BeObject;


/**
 * 
 * @author pc
 *
 */
public class NoteMgDatabase extends H3MgHandler<BeBranch> {

	
	
	public final BeCodebase codebase;
	
	public final Path path;
	
	private final IOModule ioModule = new IOModule(this);
	
	public NoteMgDatabase(SiliconEngine ng, BeCodebase codebase, Path path) {
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
	public List<H3MgHandler<?>> getSubHandlers() {
		return new ArrayList<>();
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
	
	
	
	/**
	 * 
	 * @param <T>
	 * @param t
	 * @param filter
	 * @param onSelected
	 * @param onFailed
	 */
	public <T> void select(long t, S8Filter<T> filter, 
			MgCallback<List<T>> onSelected, 
			ExceptionMgCallback onFailed) {
		pushOperation(new BrowseOp<T>(t, this, filter, onSelected, onFailed));
	}
	
}
