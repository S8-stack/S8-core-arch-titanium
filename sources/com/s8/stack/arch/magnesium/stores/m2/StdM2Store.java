package com.s8.stack.arch.magnesium.stores.m2;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;

/**
 * 
 * @author pierreconvert
 *
 * @param <K>
 */
public class StdM2Store<K> extends M2Store<K> {

	
	private boolean isLoaded = false;
	
	public StdM2Store(M2Prototype<K> prototype, Path root, int dimension, boolean isVerbose) {
		super(prototype, root, dimension, isVerbose);

	}
	


	/**
	 * 
	 * @param index0
	 */
	public void boot(long index0) {
		if(!isLoaded) {
			initialize(index0);
			isLoaded = true;
		}
	}
	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		if(!isLoaded) {
			LinkedBytes bytesHead = LinkedBytesIO.read(path, false);
			LinkedByteInflow inflow = new LinkedByteInflow(bytesHead);
			deserialize(inflow);
			isLoaded = true;
		}
	}
	

	/**
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		if(isLoaded) {
			LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
			
			serialize(outflow);
			
			LinkedBytes bytes = outflow.getHead();
			LinkedBytesIO.write(bytes, path, false);
			
			head.saveBody(this);
		}
	}

}
