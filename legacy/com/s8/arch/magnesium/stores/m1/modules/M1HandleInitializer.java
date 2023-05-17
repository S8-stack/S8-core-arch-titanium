package com.s8.arch.magnesium.stores.m1.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import com.s8.arch.magnesium.handlers.h2.H2Handle;
import com.s8.arch.magnesium.stores.m1.M1Handle;
import com.s8.arch.magnesium.stores.m1.M1ModelConstructor;
import com.s8.arch.magnesium.stores.m1.M1Store;
import com.s8.arch.silicon.SiException;

/**
 * 
 * @author pierreconvert
 *
 */
public class M1HandleInitializer<M> {

	

	/**
	 * 
	 */
	private final H2Handle.Props props;
	
	
	/**
	 * 
	 */
	private final M1Store<M> store;
	
	
	
	/**
	 * 
	 * @param store
	 * @param factory
	 */
	public M1HandleInitializer(M1Store<M> store, H2Handle.Props props) {
		super();
		this.store = store;
		this.props = props;
	}
	
	
	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	public class Load implements Function<String, M1Handle<M>> {
		
		public Load() {
			super();
		}

		@Override
		public M1Handle<M> apply(String address) {
			
			// path
			Path path = store.pathComposer.getPath(address);
			
			// create handler
			M1Handle<M> handler = new M1Handle<M>(props, store, path, address);
			
			handler.DEBUG_log = "load";
			
			return handler;
		}
		
	}
	
	
	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	public class Init implements Function<String, M1Handle<M>> {
		
		private final M1ModelConstructor<M> constructor;
		
		public Init(M1ModelConstructor<M> initializer) {
			super();
			this.constructor = initializer;
		}
	

		@Override
		public M1Handle<M> apply(String address) {
			
			Path path = store.pathComposer.getPath(address);
			
			// create handler
			M1Handle<M> handler = new M1Handle<M>(props, store, path, address);
		
			// creat first
		
			try {
				M model = constructor.init(path, address);
				// initialize
				handler.initializeReady(model);
			} 
			catch (IOException | SiException e) {
				handler.initializeFailed(new SiException(0, e.getMessage()));
			}
			
			return handler;
		}
	}

}
