package com.s8.core.arch.magnesium.databases.record;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.record.objects.RecordS8Object;
import com.s8.api.flow.record.requests.GetRecordS8Request;
import com.s8.api.flow.record.requests.PutRecordS8Request;
import com.s8.api.flow.record.requests.SelectRecordsS8Request;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.beryllium.branch.BeBranch;
import com.s8.core.bohr.beryllium.codebase.BeCodebase;


/**
 * 
 * @author pc
 *
 */
public class RecordsMgDatabase extends H3MgHandler<BeBranch> {

	public final static String DATA_FILENAME = "records-data.be";
	
	
	public final BeCodebase codebase;
	
	public final Path rootFolderPath;
	
	private final IOModule ioModule = new IOModule(this);
	
	public RecordsMgDatabase(SiliconEngine ng, BeCodebase codebase, Path rootFolderPath) {
		super(ng);
		
		this.codebase = codebase;
		this.rootFolderPath = rootFolderPath;
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

	public Path getRootFolderPath() {
		return rootFolderPath;
	}
	
	public Path getDataFilePath() {
		return rootFolderPath.resolve(DATA_FILENAME);
	}
	
	
	public void get(long t, DbMgCallback callback, GetRecordS8Request request) {
		pushOpLast(new GetOp(t, callback, this, request));
	}
	
	
	public void put(long t, DbMgCallback callback, PutRecordS8Request request) {
		pushOpLast(new PutOp(t, callback, this, request));
	}
	
	
	
	/**
	 * 
	 * @param <T>
	 * @param t
	 * @param filter
	 * @param onSelected
	 * @param onFailed
	 */
	public <T extends RecordS8Object> void select(long t, DbMgCallback callback, SelectRecordsS8Request<T> request) {
		pushOpLast(new BrowseOp<T>(t, callback, this, request));
	}
	
	
	
}
