package com.s8.arch.magnesium.handlers.h3;

import com.s8.arch.silicon.async.AsyncSiTask;
import com.s8.arch.silicon.async.MthProfile;


/**
 * 
 * @author pierreconvert
 *
 */
public class LoadMgAsyncTask<R> implements AsyncSiTask {

	public final H3MgHandler<R> handler;

	public LoadMgAsyncTask(H3MgHandler<R> handler) {
		super();
		this.handler = handler;
	}


	public @Override MthProfile profile() { return MthProfile.IO_SSD; }


	@Override
	public void run() {
		try {

			/* retriev resource */
			R resource =  handler.getIOModule().load();

			/* set resource */
			handler.setLoaded(resource);

			/* continue pumping operations */
			handler.roll(true);

		} 
		catch (Exception e) {

			handler.setFailed(e);

			/* continue pumping operations */
			handler.roll(true);
		}	
	}

	@Override
	public String describe() {
		return "Load "+handler.getName()+" resources ...";
	}


}
