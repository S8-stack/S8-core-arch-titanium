package com.s8.stack.arch.tests.db.mappings.m4;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.silicon.SiException;
import com.s8.stack.arch.magnesium.stores.m4.M4Store.Inspector;


/**
 * 
 * @author pierreconvert
 *
 */
public class M4StoreTest02 {


	/**
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws SiException 
	 */
	public static void main(String[] args) throws IOException, SiException {
		new M4StoreTest02(23721, 43, Path.of("data/m4/")).run01();
	}



	public void run01() throws IOException, SiException {

		for(int i=0; i<n; i++) {
			store.appendTransition(new DemoFlow(deltas[i]));	
		}
		System.out.println("done");
		store.save();
		System.out.println("saved");

		checkFlows();



		store = new DemoM4Store(path, p, false);
		store.load();
		checkFlows();		 
	}


	private Path path;

	private DemoM4Store store;

	private int n, p;

	private long position0;

	private long[] positions;

	private long[] deltas;



	public M4StoreTest02(int n, int p, Path path) throws SiException, IOException {
		super();

		this.n = n;
		this.p = p;

		this.positions = new long[n];
		this.deltas = new long[n];

		position0 = 198;

		long position = position0, delta = 1;
		for(int i = 0; i<n; i++) {
			//delta = -3 * delta + 7;
			delta = 1;
			deltas[i] = delta;
			position = position + delta;
			positions[i] = position;
		}


		this.path = path;
		store = new DemoM4Store(path, p, false);
		store.boot(0L, new DemoStock(position0));

	}




	/**
	 * 
	 * @throws IOException
	 */
	public void checkFlows() throws IOException {

		store.traverse(new Inspector<DemoFlow>() {

			private int i = 0;
			public @Override void accept(long index, DemoFlow flow) {
				long val = flow.delta;
				long ref = deltas[i++];
				if(((long) val)!= ref) {
					System.out.println("error for index="+i+", found="+val+", expected="+ref);	
				}
			}
		});	
		System.out.println("Traversing DONE...");
	}




}
