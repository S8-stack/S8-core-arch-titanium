package com.s8.arch.magnesium.stores.m1;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import com.s8.arch.magnesium.core.paths.ShellPathComposer;
import com.s8.arch.magnesium.handlers.h2.H2Handle;
import com.s8.arch.magnesium.handlers.h2.H2Operator;
import com.s8.arch.magnesium.stores.m1.modules.Clock;
import com.s8.arch.magnesium.stores.m1.modules.M1HandleInitializer;
import com.s8.arch.magnesium.stores.m1.modules.M1StoreAutosaver;
import com.s8.arch.magnesium.stores.m1.modules.M1StoreLightener;
import com.s8.arch.magnesium.stores.m1.modules.M1StoreModelsSaver;
import com.s8.arch.magnesium.stores.m1.modules.StoreAddressManager;
import com.s8.arch.silicon.SiliconEngine;
import com.s8.io.bytes.ByteFileReadingException;
import com.s8.io.bytes.ByteFileWritingException;

/**
 * 
 * @author pierreconvert
 *
 */
public class M1Store<M> {


	public static final int MAX_NUMBER_BLOCK_TYPES = 256;

	public static final int FILE_BUFFER_CAPACITY = 1024;

	public static final int TYPICAL_NB_PARALLEL_SHELLS = 64;

	
	/**
	 * 
	 */
	public static final int CAPACITY_BYTECOUNT = 1048576;


	/**
	 * 
	 */
	public final SiliconEngine ng;

	public final M1ModelPrototype<M> prototype;

	private final Clock<M> clock;

	private final M1HandleInitializer<M> initializer;

	private final M1StoreLightener<M> storeLightener;

	private final M1StoreModelsSaver<M> storeModelsSaver;
	
	private final M1StoreAutosaver<M> storeAutosaver;

	private final StoreAddressManager addressManager;

	public final ShellPathComposer pathComposer;


	/**
	 * loaded shells
	 */
	final ConcurrentHashMap<String, M1Handle<M>> models;


	/**
	 * 
	 */
	private final boolean isVerbose;


	private final long capacity;


	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	public static class Props {

		
		/**
		 * max number of modelds simultaneously loaded
		 */
		public long capacity = CAPACITY_BYTECOUNT;
		
		public int folderDepth = 3;

		public boolean isVerbose = true;

		public H2Handle.Props handlerProps = new H2Handle.Props();
	}


	public class Facet {


		/**
		 * Direct access to shell handler. Shell handler is created if not already present.
		 * @param address
		 * @return
		 */
		public M1Handle<M> getModelHandler0(String address) {
			return models.get(address);
		}


		/**
		 * 
		 * @param crawler
		 */
		public void traverse(BiConsumer<String, M1Handle<M>> crawler) {
			models.forEach(crawler);
		}


		public SiliconEngine getEngine() {
			return ng;
		}
		
		public void save() throws ByteFileWritingException, IOException {
			M1Store.this.save();
		}


		public boolean isVerbose() {
			return M1Store.this.isVerbose;
		}

	}


	/**
	 * 
	 * @param root
	 * @param mapping
	 * @param service
	 * @param gcTrigger
	 * @param SSL_isVerbose
	 * @throws ByteFileReadingException
	 * @throws IOException
	 */
	public M1Store(Props props, SiliconEngine ng, M1ModelPrototype<M> prototype, Path root) throws ByteFileReadingException, IOException {
		super();

		Facet facet = new Facet();

		/* <props> */
		this.isVerbose = props.isVerbose;
		this.capacity = props.capacity;
		/* </props> */


		this.ng = ng;
		this.prototype = prototype;


		// create main shells map
		models = new ConcurrentHashMap<String, M1Handle<M>>(TYPICAL_NB_PARALLEL_SHELLS);


		clock = new Clock<M>(facet);


		/* modules */
		initializer = new M1HandleInitializer<>(this, props.handlerProps);

		// store lightener
		storeLightener = new M1StoreLightener<>(facet, capacity, props.isVerbose);

		// store saver
		storeModelsSaver = new M1StoreModelsSaver<>(facet);
		
		// store auto-saver
		storeAutosaver = new M1StoreAutosaver<>(facet);
		
		// address
		addressManager = new StoreAddressManager(root);

		// path composer
		pathComposer = new ShellPathComposer(root, props.folderDepth, ".nd");

	}



	/**
	 * 
	 * @param consumer
	 */
	public void forModel(String address, H2Operator<M> operator) {
		
		// handler
		M1Handle<M> handler =  models.computeIfAbsent(address, initializer.new Load());

		// update timestamp
		handler.timestamp = clock.getTimestamp();

		// handler
		handler.operate(operator);

		// trigger lighten
		storeLightener.lighten();
	}
	

	/**
	 * 
	 * @param address
	 * @param constructor
	 * @return
	 */
	public String initModel(String address, M1ModelConstructor<M> constructor) {
		
		// allocate handler for this address
		M1Handle<M> handle =  models.computeIfAbsent(address, initializer.new Init(constructor));
		
		// request save
		handle.requestSave();

		// update timestamp
		handle.timestamp = clock.getTimestamp();
		
		// trigger lighten
		storeLightener.lighten();
		
		// address
		return address;
	}



	
	/**
	 * 
	 * @param repositoryAddress
	 * @param model
	 */
	public String createNewModel(M1ModelConstructor<M> constructor) {
		
		// create new address
		String address = addressManager.getNewAddress();
		
		// allocate handler for this address
		M1Handle<M> handle =  models.computeIfAbsent(address, initializer.new Init(constructor));
		
		// request save
		handle.requestSave();

		// update timestamp
		handle.timestamp = clock.getTimestamp();
		
		// trigger lighten
		storeLightener.lighten();
		
		// address
		return address;
	}



	/**
	 * 
	 * @param address
	 * @return
	 */
	public Path resolvePath(String address) {
		return pathComposer.getPath(address);
	}


	/**
	 * <p>Directly put a resolved block in the store, erasing previous one if any.</p>
	 * <p><b>/!\ Danger</b>: this method is intended solely for immutable insertion</p>
	 * @param block
	 */
	/*
	public void put(DbBlock block) {
		blocks.put(block.getPath(), block);
	}
	 */




	public void displayMappedBuckets(String generating) {
		if(isVerbose) {
			StringBuilder builder = new StringBuilder();
			builder.append("[db-server] "+generating+"> mapped buckets: ");
			models.forEach((k,b) -> { 
				builder.append(k+", ");
			});
			System.out.println(builder.toString());
		}
	}


	/**
	 * 
	 * @throws IOException 
	 * @throws ByteFileWritingException 
	 * @throws Exception
	 */
	public void save() throws ByteFileWritingException, IOException {
		storeModelsSaver.save();
		addressManager.save();
	}

	
	public void startAutosave() {
		storeAutosaver.start();
	}
	
	public void stopAutosave() {
		storeAutosaver.stop();
	}

}
