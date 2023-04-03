package com.s8.stack.arch.tests.db.mappings.m2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.stack.arch.magnesium.stores.m2.M2Prototype;
import com.s8.stack.arch.magnesium.stores.m2.StdM2Store;

public class M2StoreTest01 {


	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		M2Prototype<Double> proto = new M2Prototype<Double>() {

			@Override
			public void serialize(Double value, ByteOutflow outflow) throws IOException {
				outflow.putFloat64(value);
			}

			@Override
			public Class<Double> getRawType() {
				return Double.class;
			}

			@Override
			public Double deserialize(ByteInflow inflow) throws IOException {
				return inflow.getFloat64();
			}
		};


		StdM2Store<Double> store = new StdM2Store<Double>(proto, Path.of("data/"), 4, false);
		store.boot(0L);

		
		int dimension = 4;
		int p = 1731;
		int n = p*dimension;
		
		long c=0;
		for(int i=0; i<p; i++) {
			for(int j=0; j<dimension; j++) {
				store.add((double) (c++));	
			}
		}
		System.out.println("done");
		store.save();
		System.out.println("saved");

		store.traverse(new Consumer<Double>() {

			private long c = 0;
			public @Override void accept(Double value) {
				double val = value;
				if(((long) val)!=c++) {
					System.out.println("error "+c);	
				}
			}
		});	
		System.out.println("Traversing DONE...");

		store = new StdM2Store<Double>(proto, Path.of("data/"), 4, false);
		store.load();
		store.traverse(new Consumer<Double>() {

			private long c = 0;
			public @Override void accept(Double value) {
				double val = value;
				if(((long) val)!=c++) {
					System.out.println("error "+c);	
				}
			}
		});
		System.out.println("Traversing DONE...");

		
		for(int k=0; k<1000; k++) {
			int i = (int) (Math.random()*n);
			double val = store.get(i);

			//System.out.println("Testing: "+i);
			if(((int) val) != i) {
				System.out.println("Discrepancy");
			}
		}
	}

}
