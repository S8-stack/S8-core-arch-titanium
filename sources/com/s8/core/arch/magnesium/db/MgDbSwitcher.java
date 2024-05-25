package com.s8.core.arch.magnesium.db;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.s8.core.arch.magnesium.db.requests.MgRequest;
import com.s8.core.arch.silicon.SiliconEngine;


public class MgDbSwitcher<R> {
	
	
	private final SiliconEngine ng;
	
	private final Object lock = new Object();
	/**
	 * 
	 */
	private final Map<String, MgDbHandler<R>> map = new ConcurrentHashMap<>();
	

	private final Queue<MgRequest<R>> queue = new ConcurrentLinkedQueue<>();
	
	
	final MgPathComposer pathComposer;
	
	final MgIOModule<R> ioModule;
	
	private volatile String operatedKey = null;

	
	public MgDbSwitcher(SiliconEngine ng, MgPathComposer pathComposer, MgIOModule<R> ioModule) {
		super();
		this.ng = ng;
		this.pathComposer = pathComposer;
		this.ioModule = ioModule;
	}
	
	
	
	public void processRequest(MgRequest<R> request) {
		
		boolean hasBeenParked = false;
		
		synchronized (lock) {
			if(operatedKey != null && operatedKey.equals(request.key)) {
				queue.add(request);
				hasBeenParked = true;
			}
		}
		
		if(!hasBeenParked) {
		
			/* retrieve key */
			String key = request.key;
			
			/* retrieve handler (creating it if necessary) */
			MgDbHandler<R> handler = map.computeIfAbsent(key, k -> new MgDbHandler<>(ng, this, k));
			
			/* make it process request */
			handler.processRequest(request);
		}
	}
	
	
	
	void unpark() {
		while(!queue.isEmpty()) {
			processRequest(queue.poll());
		}
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
