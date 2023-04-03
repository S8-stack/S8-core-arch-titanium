package com.s8.stack.arch.tests.db.mappings.m1;

import java.io.IOException;

import com.s8.arch.magnesium.handles.h2.H2Handle;
import com.s8.io.bytes.ByteFileReadingException;

public class M1StoreTest03 {

	public static void main(String[] args) throws ByteFileReadingException, IOException, InterruptedException {
		new M1StoreTest03().run();
	}

	
	
	public void run() throws IOException, InterruptedException {
		M1TestContext context = new M1TestContext(
				256000,// number of values in the db
				4, // number of test threads
				2500, // number of calls
				65536*64); // map capacity (in Bytes) : roughly 40 bytes per objects
		context.startLoaders();
		//startDebugMetrics();
		//context.finallySave();
		
	}
	
	
	public M1StoreTest03() throws ByteFileReadingException, IOException {
		super();
	}
	
	
	/*
	 * 
	 */
	public void startDebugMetrics() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					System.out.println("Nb of files saved: "+H2Handle.DEBUG_nSaved);
					System.out.println("Nb of changes: "+HeavyLoader.DEBUG_nChanges.get());
					System.out.println("Nb of discrepancies: "+HeavyLoader.DEBUG_nDiscrepancies.get());
					try {
						Thread.sleep(2000);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
}
