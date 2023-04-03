package com.s8.stack.arch.magnesium.stores.m1.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.s8.io.bytes.ByteFile;
import com.s8.io.bytes.ByteFileLoader;
import com.s8.io.bytes.ByteFileWritingException;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.base64.Base64Generator;


/**
 * Max garbage collection is 16M paths.
 * 
 * @author pc
 *
 */
public class StoreAddressManager extends ByteFile {

	private final static String PATHNAME = "paths/index";

	private final static byte[] OPENING_TAG = "<state:>".getBytes();

	private final static byte[] CLOSING_TAG = "</state>".getBytes();


	private Lock lock;

	private long index;
	
	private Base64Generator generator;
	
	public StoreAddressManager(Path root) throws IOException {
		super(root.resolve(PATHNAME));
		
		lock = new ReentrantLock();
		
		generator = new Base64Generator("node-");
		
		if(!isExisting()) { 
			
			// not existing, so initialize (and save)
			initialize();
		}
		else {
			// else, load...
			boolean isSuccefullyLoaded = load();
			
			if(!isSuccefullyLoaded) {
				initialize();
			}
		}
	}
	

	
	private class Loader extends ByteFileLoader {
		
		public boolean isSuccessfullyLoaded = false;
		
		public Loader(Path path) {
			super(path);
		}
		
		@Override
		public void onIOException(IOException exception) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onFileDoesNotExist() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void read(ByteInflow inflow) throws IOException {
			if(!inflow.matches(OPENING_TAG)) {
				throw new IOException("Failed to match header code");
			}
			
			long deserial = inflow.getUInt64();
			
			lock.lock();
			
			index = deserial;
			
			lock.unlock();
			
			if(!inflow.matches(CLOSING_TAG)) {
				throw new IOException("Failed to match footer code");
			}
			isSuccessfullyLoaded = true;
		}
		
	}
	
	
	private boolean load() {
		Loader loader = new Loader(getPath());
		loader.load(getBufferCapacity());
		return loader.isSuccessfullyLoaded;
	}
	
	
	
	private void initialize() throws ByteFileWritingException, IOException {
		
		lock.lock();
		index = 0x02fL;
		lock.unlock();
		
		save();
	}

	@Override
	public void write(ByteOutflow outflow) throws IOException {

		// header
		outflow.putByteArray(OPENING_TAG);

		// output
		lock.lock();
		
		outflow.putUInt64(index);
		
		lock.unlock();

		// footer
		outflow.putByteArray(CLOSING_TAG);
	}
	
	
	
	/**
	 * Thread safe
	 * @return
	 */
	public String getNewAddress() {
		
		lock.lock();
		
		String address = generator.generate(index++);
		
		lock.unlock();
		
		return address;
	}

}
