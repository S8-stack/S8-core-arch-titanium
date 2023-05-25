package com.s8.stack.arch.tests.db.h2;

public class AutoCloseBot {

	public final String name;

	public final int index;

	public final H2TestContext context;

	public final int nOps;





	public boolean hasError;

	/**
	 * 
	 * @param context
	 */
	public AutoCloseBot(String name, int index, H2TestContext context, int nOps) {
		super();
		this.name = name;
		this.index = index;
		this.context = context;
		this.nOps = nOps;
	}

	
	
	public void start() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0; i<nOps; i++) {
					context.getHandle().requestShutDown(null);
					try {
						Thread.sleep(250);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				}
				
				
				System.out.println("[Bot0] name="+name+": NOW COMPLETED. Has errors?: "+hasError);
			}
		});
		thread.start();
	}



}
