package com.s8.core.arch.titanium.db;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.arch.titanium.db.requests.TiRequest;


public abstract class TiDbSwitcher<R> {
	
	
	private final SiliconEngine ng;
	
	private final Object lock = new Object();
	/**
	 * 
	 */
	private final Map<String, TiDbHandler<R>> map = new ConcurrentHashMap<>();
	

	private final Queue<TiRequest<R>> queue = new ConcurrentLinkedQueue<>();
	
	
	final TiPathComposer pathComposer;
	
	private volatile String frozenKey = null;

	
	public TiDbSwitcher(SiliconEngine ng, TiPathComposer pathComposer) {
		super();
		this.ng = ng;
		this.pathComposer = pathComposer;
	}

	
	/**
	 * 
	 * @return
	 */
	public abstract TitaniumIOModule<R> getIOModule();
	
	
	
	public void processRequest(TiRequest<R> request) {
		
		boolean hasBeenParked = false;
		
		synchronized (lock) {
			if(frozenKey != null && frozenKey.equals(request.mgKey)) {
				queue.add(request);
				hasBeenParked = true;
			}
		}
		
		if(!hasBeenParked) {
		
			/* retrieve key */
			String key = request.mgKey;
			
			/* retrieve handler (creating it if necessary) */
			TiDbHandler<R> handler = map.computeIfAbsent(key, k -> new TiDbHandler<>(ng, this, k));
			
			/* make it process request */
			handler.processRequest(request);
		}
	}
	
	
	
	void unpark() {
		while(!queue.isEmpty()) {
			processRequest(queue.poll());
		}
	}
	
	
	public void freeze(String key) {
		/* freeze key */
		synchronized (lock) { frozenKey = key; }
	}
	
	
	public void unfreeze() {
		synchronized (lock) { frozenKey = null; }
	}
	
	public void save() {
		map.forEach((key, handler) -> handler.save());
	}
	
	/*
	private volatile long nextWakeUp = 0;
	
	public void startCulling() {
		ng.pushClockTask(new ClockSiTask() {
			
			@Override
			public boolean trigger(long t, SiliconEngine engine) {
				if(t > nextWakeUp) {
					
					nextWakeUp = t + 
				}
				return false;
			}
		});
	}
	*/
	
}
