package com.s8.stack.arch.tests.db.h2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.s8.arch.magnesium.handlers.h2.H2Operator;
import com.s8.arch.silicon.SiException;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.blocks.bytes.demos.d0.FatDemoFile02;

public class H2TestBot {

	public final String name;

	public final int index;

	public final H2TestContext context;

	public final int nOps;

	public final BlockingQueue<Task> tasks;



	private long time;

	public boolean hasError;

	/**
	 * 
	 * @param context
	 */
	public H2TestBot(String name, int index, H2TestContext context, int nOps) {
		super();
		this.name = name;
		this.index = index;
		this.context = context;
		this.nOps = nOps;

		this.tasks = new LinkedBlockingDeque<Task>();
		time = 64;
	}

	
	
	public void start() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0; i<nOps; i++) {
					if(i%100 == 0) {
						System.out.println("[Bot0] name="+name+", step="+i);
					}
					try {
						tasks.take().perform();

						// wait
						
						time = time * 103 + 57; if(time<0) { time = -time; }
						long sleepTime = (i%100 != 0) ? time%8 : 1000;
						try {
							Thread.sleep(sleepTime);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println("[Bot0] name="+name+": NOW COMPLETED. Has errors?: "+hasError);
			}
		});
		thread.start();
		next();
	}


	public void next() {
		tasks.add(new Task());
	}
	
	public class Task {

		public void perform() {
			context.getHandle().operate(new H2Operator<FatDemoFile02>() {


				public @Override MthProfile profile() { return MthProfile.IO_DATA_LAKE; }

				@Override
				public void onReady(FatDemoFile02 asset) {
					if(asset.checkSum!=context.checkSum) {
						hasError = true;
					}
					//System.out.println("Acquired asset!!");
					next();
				}					

				@Override
				public void onFailed(SiException error) {
					hasError = true;
					System.err.println("******/!\\*****: Failed");
				}
			});
		}
	}


}
