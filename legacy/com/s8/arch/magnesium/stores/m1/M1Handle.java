package com.s8.arch.magnesium.stores.m1;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.magnesium.handles.h2.H2Handle;
import com.s8.arch.magnesium.handles.h2.H2ModelPrototype;
import com.s8.arch.magnesium.handles.h2.H2Operator;
import com.s8.arch.silicon.SiliconEngine;


/**
 * 
 * @author pierreconvert
 *
 */
public class M1Handle<M> extends H2Handle<M> {
	
	private class LocalPrototype implements H2ModelPrototype<M> {

		private final M1ModelPrototype<M> prototype;
		
		public LocalPrototype(M1ModelPrototype<M> prototype) {
			super();
			this.prototype = prototype;
		}

		@Override
		public M load() throws IOException {
			return prototype.load(path, address);
		}

		@Override
		public void save(M model) throws IOException {
			prototype.save(path, model);
		}
		
		@Override
		public long getBytecount(M model) {
			return prototype.getBytecount(model);
		}
	}
	
	

	private final M1Store<M> store;

	private final LocalPrototype prototype;
	
	private final Path path;
		
	private final String address;
	
	
	public long timestamp;
	
	
	/**
	 * 
	 * @param props
	 * @param options
	 * @param store
	 * @param path
	 * @param address
	 */
	public M1Handle(Props props, M1Store<M> store, Path path, String address) {
		super(props);
		this.store = store;

		this.address = address;
		this.path = path;
		this.prototype = new LocalPrototype(store.prototype);
	}
	

	@Override
	public H2ModelPrototype<M> getPrototype() {
		return prototype;
	}
	
	
	
	
	
	/**
	 * 
	 * @param request
	 */
	//public abstract void serve(LthRequest request);


	@Override
	public void detach() {
		store.models.remove(address);
	}


	@Override
	public SiliconEngine getAppEngine() {
		return store.ng;
	}


	@Override
	public void reroute(H2Operator<M> operator) {
		store.forModel(address, operator);
	}

	@Override
	public String describe() {
		return address.toString();
	}
	
}
