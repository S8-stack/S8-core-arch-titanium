package com.s8.core.arch.magnesium.handlers.h2;

public interface C0Callback {




	public final static int SUCCESSFUL = 0x00;

	public final static int FAILED = 0x03;


	/**
	 * 
	 * @param code termination code (0x00 = SUCCESSFUL)
	 */
	public void onTerminated(int code);


	
	
	/**
	 * 
	 */
	public final static C0Callback NO_ACTION = new C0Callback() {

		@Override
		public void onTerminated(int code) {
			// do nothing
		}
	};

}
