package com.s8.stack.arch.tests.db.h2;

/**
 * 
 * @author pierreconvert
 *
 */
public class H2HandleTesting {


	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		H2TestContext context = new H2TestContext();
		context.run(6, 4000);
	}

		

}
