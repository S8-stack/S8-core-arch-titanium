package com.s8.stack.arch.tests.db.mappings.m1;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.magnesium.stores.m1.M1ModelConstructor;
import com.s8.arch.magnesium.stores.m1.M1Store;
import com.s8.arch.silicon.SiliconConfiguration;
import com.s8.arch.silicon.SiliconEngine;

public class M1TestContext {


	public final BenchValue[] benchValues;

	public final HeavyLoader[] loaders;
	
	public final int[] randomPicks;
	
	public volatile int iPick = 0;

	public final int nTestThreads;

	public final int nValues;

	public final SiliconEngine ng;

	public final M1Store<PseudoRepo> store;

	public M1TestContext(int nValues, int nTestThreads, int nCalls, int capacity) throws IOException {
		super();

		this.nValues = nValues;
		this.nTestThreads = nTestThreads;


		SiliconConfiguration config = new SiliconConfiguration();
		ng = new SiliconEngine(config);



		ng.start();


		
		int p = nTestThreads*nCalls*2;
		randomPicks = new int[p];
		for(int i=0; i<p; i++) { randomPicks[i] = (int) (Math.random() * nValues); }

		/* <initializ store> */
		M1Store.Props props = new M1Store.Props();
		props.handlerProps.isVerbose = false;
		props.capacity = capacity;
		props.isVerbose = true;


		store = new M1Store<PseudoRepo>(props, ng, PseudoRepo.PROTOTYPE, Path.of("data/m1/"));


		System.out.println("Creating bench values and storing them into db...");
		benchValues = new BenchValue[nValues];
		for(int i=0; i<nValues; i++) {
			int iThread = (int) (Math.random()*nTestThreads);

			int iCopy = i;
			String val = RandomStringGenerator.generate();
			String address = store.createNewModel(new M1ModelConstructor<PseudoRepo>() {

				@Override
				public PseudoRepo init(Path path, String address) {
					System.out.println("Value created: "+iCopy);
					return new PseudoRepo(val);
				}
			});
			benchValues[i] = new BenchValue(address, val, iThread);	
		}

		store.save();
		/* </initialize store> */

		loaders = new HeavyLoader[nTestThreads];
		
		for(int i=0; i<nTestThreads; i++) {
			loaders[i] = new HeavyLoader(store, this, i, nCalls);
		}
		// just to be sure all async call terminate
		try {
			Thread.sleep(64);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void startLoaders() {
		

		System.out.println("Run loaders...");
		for(int i=0; i<nTestThreads; i++) {
			loaders[i].start();
		}
	}

	
	public void finallySave() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(20000);
						System.out.println("Saving store");
						store.save();
					} 
					catch (InterruptedException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public synchronized BenchValue pick() {
		int i = randomPicks[iPick++];
		return benchValues[i];
	}
}
