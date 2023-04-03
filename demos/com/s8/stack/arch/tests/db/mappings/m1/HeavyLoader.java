package com.s8.stack.arch.tests.db.mappings.m1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.s8.arch.magnesium.handles.h2.H2Operator;
import com.s8.arch.magnesium.stores.m1.M1Store;
import com.s8.arch.silicon.SiException;
import com.s8.arch.silicon.async.MthProfile;

public class HeavyLoader {


	public static AtomicLong DEBUG_nChanges = new AtomicLong();
	public static AtomicLong DEBUG_nDiscrepancies = new AtomicLong();

	private final M1Store<PseudoRepo> store;

	private final M1TestContext context;

	private final int iThread;

	private final int nCalls;


	private BlockingQueue<Task> tasks;

	public HeavyLoader(M1Store<PseudoRepo> store, M1TestContext context, int iThread, int nCalls) {
		super();
		this.store = store;
		this.context = context;
		this.iThread = iThread;
		this.nCalls = nCalls;

		tasks = new LinkedBlockingQueue<Task>();
	}




	public void start() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				int count = 0;			
				while(count++ < nCalls) {
					if(count%1000==0) {
						System.out.println(">Thread Loader ["+iThread+"]: in progress: "+count);
					}
					try {
						tasks.take().peform();
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("**** Thread Loader ["+iThread+"] ****");
				System.out.println(">> Terminated= "+count);
			}

		});
		thread.start();

		proceed();
	}


	public void proceed() {
		tasks.add(new Task(context.pick()));
	}




	public class Task {

		private BenchValue resource;

		public Task(BenchValue resource) {
			super();
			this.resource = resource;
		}

		public void peform() {

			// initialize
			store.forModel(resource.address, new H2Operator<PseudoRepo>() {

				@Override
				public MthProfile profile() {
					return MthProfile.FX0;
				}

				@Override
				public void onReady(PseudoRepo asset) {
					if(!asset.value.equals(resource.defaultValue)) {
						System.out.println("defualt (expected) value is: "+resource.defaultValue);
						System.out.println("Current repo value is: "+asset.value);
						DEBUG_nDiscrepancies.getAndIncrement();
						throw new RuntimeException("Discrepancy!");

					}
					else {
						proceed();
					}
				}

				@Override
				public void onFailed(SiException error) {
					error.printStackTrace();
				}
			});
		}
	}

}
