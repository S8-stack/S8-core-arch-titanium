package com.s8.arch.magnesium.stores.m1.modules;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.s8.arch.magnesium.stores.m1.M1Store;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.arch.silicon.clock.ClockTask;
import com.s8.io.bytes.ByteFileWritingException;

public class M1StoreAutosaver<M> {

	private final M1Store<M>.Facet facet;
	
	private AtomicBoolean isAutosaving;
	
	
	public M1StoreAutosaver(M1Store<M>.Facet facet) {
		super();
		this.facet = facet;
		this.isAutosaving = new AtomicBoolean();
	}
	
	
	
	public void start() {
		isAutosaving.set(true);
		facet.getEngine().pushT3Task(new ClockTask() {

			@Override
			public boolean trigger(long t, SiliconEngine engine) {
				if(t%8 == 0) { // save 4 ticks (upon standard settings : ~2s)
					engine.pushT1Task(new AsyncTask() {
						public @Override MthProfile profile() { return MthProfile.IO_DATA_LAKE; }
						public @Override String describe() { return "[silicon/M1Store] AUTO_SAVING"; }

						@Override
						public void run() {
							try {
								facet.save();
							} 
							catch (ByteFileWritingException e) {
								e.printStackTrace();
							} 
							catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
				return isAutosaving.get(); // always continued while autosaving is on
			}
		});
	}
	
	
	public void stop() {
		isAutosaving.set(false);
	}
	
}
