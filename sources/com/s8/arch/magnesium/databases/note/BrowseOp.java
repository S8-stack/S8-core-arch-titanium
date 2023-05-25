package com.s8.arch.magnesium.databases.note;

import java.util.List;

import com.s8.arch.fluor.S8Filter;
import com.s8.arch.magnesium.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.exception.BeIOException;

public class BrowseOp<T> extends UserH3MgOperation<BeBranch> {
	
	public final NoteMgDatabase handler;
	
	
	public final S8Filter<T> filter;
	
	public final MgCallback<List<T>> onSelected;
	
	public final ExceptionMgCallback onFailed;

	public BrowseOp(long timeStamp, NoteMgDatabase handler, 
			S8Filter<T> filter,
			MgCallback<List<T>> onSelected, 
			ExceptionMgCallback onFailed) {
		super(timeStamp);
		this.handler = handler;
		this.filter = filter;
		this.onSelected = onSelected;
		this.onFailed = onFailed;
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
				try {
					List<T> objects = branch.select(filter);
					onSelected.call(objects);
					
				} catch (BeIOException e) {
					e.printStackTrace();
					onFailed.call(e);
				}
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
				onFailed.call(exception);	
			}
		};
	}

	@Override
	public boolean isModifyingResource() {
		return false;
	}

}
