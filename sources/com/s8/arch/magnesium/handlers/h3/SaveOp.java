package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 * @param <R>
 */
public class SaveOp<R> extends SystemH3MgOperation<R> {

	
	
	public SaveOp(H3MgHandler<R> handler) {
		super(handler);
	}
	

	@Override
	public boolean isModifyingResource() {
		return true;
	}

	@Override
	public AsyncTask createConsumeResourceTask(R resource) {
		return new SaveMgTask<R>(handler, resource);
	}

	
	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) {
		return new CatchExceptionMgTask(exception) {

			@Override
			public String describe() {
				return "catch exception";
			}

			@Override
			public MthProfile profile() {
				return MthProfile.FX0;
			}

			@Override
			public void catchException(Exception exception) {
				System.out.println("Save failed because of following exception:");
				exception.printStackTrace();	
			}
		};
	}

}
