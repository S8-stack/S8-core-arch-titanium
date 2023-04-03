package com.s8.stack.arch.tests.db.mappings.m3;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.stack.arch.magnesium.stores.m3.HashcodeModule;
import com.s8.stack.arch.magnesium.stores.m3.M3Config;
import com.s8.stack.arch.magnesium.stores.m3.M3ModelPrototype;
import com.s8.stack.arch.magnesium.stores.m3.M3Store;
import com.s8.stack.arch.magnesium.stores.m3.requests.GetM3Request;
import com.s8.stack.arch.magnesium.stores.m3.requests.PutM3Request;

/**
 * 
 * @author pierreconvert
 *
 */
public class M3StoreTest01 {


	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new M3StoreTest01().run();
	}
	
	private M3ModelPrototype<String> prototype;

	private int n;

	private int p;

	private String[] keys;

	private Path path;
	
	private M3Store<String> store;

	
	/**
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		
		boolean isTransactional = false;
		
		initialize(Path.of("data/m3/"), 60000, 1000);

		store = new DemoM3Store(path, prototype);
		M3Config config = new M3Config();
		config.saveFrequency = 256;
		store.boot(config);

		System.out.println("Start store filling...");
		for(int i=0; i<n; i++) {
			String key = keys[i];
			new PutM3Request<String>(store, key, key, isTransactional).serve();
			if(i%p == 0) { System.out.println("i="+i); }
		}
		System.out.println("Store half-filled!");
		store.save();

		// check
		store = new DemoM3Store(path, prototype);
		store.load();
		check(0, n, false);


		// reload
		store = new DemoM3Store(path, prototype);
		store.load();
		System.out.println("Store reloaded");
		for(int i=n; i<2*n; i++) {
			String key = keys[i];
			new PutM3Request<String>(store, key, key, isTransactional).serve();
			if(i%p == 0) { System.out.println("i="+i); }
		}
		store.save();
		System.out.println("Store filled!");

		
		System.out.println("Retrieving...");
		store = new DemoM3Store(Path.of("data/m3/"), prototype);
		store.load();
		
		System.out.println("Store reloaded");
		check(0, 2*n, false);
		
		//new BrowseM3Request<String>(store, (k, v) -> System.out.println("key: "+k+", value: "+v)).serve();
	}


	/**
	 * 
	 * @param n
	 * @param p
	 */
	public void initialize(Path path, int n, int p) {


		this.path = path;
		this.n = n;
		this.p = p;

		prototype = new M3ModelPrototype<String>() {

			@Override
			public void serialize(String value, ByteOutflow outflow) throws IOException {
				outflow.putStringUTF8(value);
			}

			@Override
			public Class<String> getBaseType() {
				return String.class;
			}

			@Override
			public String deserialize(ByteInflow inflow) throws IOException {
				return inflow.getStringUTF8();
			}
		};

		// keys
		this.keys = new String[2*n];
		for(int i=0; i<2*n; i++) {
			keys[i] = KeyGenerator.generate(16);
			//keys[i] = "key_"+i;
		}
	}
	
	
	private void check(int i0, int i1, boolean isVerbose) throws IOException {
		System.out.println("Start checking-out...");
		int m = 0;
		for(int i=i0; i<i1; i++) {
			String key = keys[i];
			String value = new GetM3Request<String>(store, key).getValue();

			if(!key.equals(value)) {
				if(isVerbose) {
					long hashcode = HashcodeModule.compute(key);
					System.out.println("Missing entry for "+key+" ("+i+") hashcode="+Long.toHexString(hashcode));
					System.out.println("\t=>value is:"+value);
					System.out.println("\tfork 0: "+ (hashcode & 0xff));
					System.out.println("\tfork 1: "+ ((hashcode>>8) & 0xff));
					System.out.println();	
				}
				m++;
			}
			if(isVerbose && (i%p == 0)) { System.out.println("\ttesting: i="+i); }
		}
		System.out.println("-> Nb of entries missing: "+m);
	}
	
	
	
}
