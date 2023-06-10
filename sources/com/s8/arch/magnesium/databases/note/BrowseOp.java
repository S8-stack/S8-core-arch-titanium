package com.s8.arch.magnesium.databases.note;

import java.util.List;

import com.s8.arch.fluor.S8Filter;
import com.s8.arch.fluor.outputs.ObjectsListS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;

public class BrowseOp<T> extends RequestH3MgOperation<BeBranch> {
	
	@Override
	public boolean isModifyingResource() { return false; }

	
	/**
	 * handler
	 */
	public final NoteMgDatabase handler;
	
	
	/**
	 * 
	 */
	public final S8Filter<T> filter;
	
	
	/**
	 * on selected
	 */
	public final MgCallback<ObjectsListS8AsyncOutput<T>> onSelected;

	
	/**
	 * options
	 */
	public final long options;
	
	public BrowseOp(long timeStamp, NoteMgDatabase handler, 
			S8Filter<T> filter,
			MgCallback<ObjectsListS8AsyncOutput<T>> onSelected, long options) {
		super(timeStamp);
		this.handler = handler;
		this.filter = filter;
		this.onSelected = onSelected;
		this.options = options;
	}
	
	

	@Override
	public ConsumeResourceMgTask<BeBranch> createConsumeResourceTask(BeBranch branch) {
		return new ConsumeResourceMgTask<BeBranch>(branch) {

			@Override
			public String describe() {
				return "login op";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public H3MgHandler<BeBranch> getHandler() {
				return handler;
			}

			@Override
			public void consumeResource(BeBranch branch) {
				ObjectsListS8AsyncOutput<T> output = new ObjectsListS8AsyncOutput<T>();
				try {
					List<T> objects = branch.select(filter);
					output.users = objects;
					output.isSuccessful = true;
					
				} catch (BeIOException e) {
					e.printStackTrace();
					output.reportException(e);
				}
				onSelected.call(output);
			}
		};
	}

	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) {
		return new CatchExceptionMgTask(exception) {
			
			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}
			
			@Override
			public String describe() {
				return "catching exception";
			}
			
			@Override
			public void catchException(Exception exception) {
				ObjectsListS8AsyncOutput<T> output = new ObjectsListS8AsyncOutput<T>();
				output.reportException(exception);
				onSelected.call(output);
			}
		};
	}


}
