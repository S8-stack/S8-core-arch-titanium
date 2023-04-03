package com.s8.arch.magnesium.stores.m3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;

/**
 * 
 * @author pierreconvert
 *
 */
public class M3Config {

	/**
	 * 
	 */
	public int nbits = 8;
	
	
	/**
	 * 
	 */
	public int threshold = 4;
	

	/**
	 * 
	 */
	public int maxNbLoaded = 1024;

	
	/**
	 * 
	 */
	public int saveFrequency = 256;

	
	/**
	 * 
	 */
	public M3Config() {
		super();
	}
	
	
	/**
	 * 	
	 * @param nbits
	 * @param maxNbLoaded
	 * @param saveFrequency
	 */
	public M3Config(int nbits, int threshold, int maxNbLoaded, int saveFrequency) {
		super();
		this.nbits = nbits;
		this.threshold = threshold;
		this.maxNbLoaded = maxNbLoaded;
		this.saveFrequency = saveFrequency;
	}





	/**
	 * 
	 */
	private final static byte[] OPENING_TAG = "<c:>".getBytes(StandardCharsets.US_ASCII);

	
	/**
	 * 
	 */
	private final static byte[] CLOSING_TAG = "</c>".getBytes(StandardCharsets.US_ASCII);



	/**
	 * 
	 * @param store
	 * @param outflow
	 * @throws IOException
	 */
	public void serialize(ByteOutflow outflow) throws IOException {
		outflow.putByteArray(OPENING_TAG);
		outflow.putUInt8(nbits);
		outflow.putInt32(threshold);
		outflow.putInt32(maxNbLoaded);
		outflow.putInt32(saveFrequency);
		outflow.putByteArray(CLOSING_TAG);		
	}



	/**
	 * 
	 * @param store
	 * @param inflow
	 * @throws IOException
	 */
	public static M3Config deserialize(ByteInflow inflow) throws IOException {

		if(!inflow.matches(OPENING_TAG)) {
			throw new IOException("Failed to match opening tag");
		}

		int nbits = inflow.getUInt8();
		int threshold = inflow.getInt32();
		int maxNbLoaded = inflow.getInt32();
		int saveFrequency = inflow.getInt32();
		
		
		if(!inflow.matches(CLOSING_TAG)) {
			throw new IOException("Failed to match opening tag");
		}
		
		return new M3Config(nbits, threshold, maxNbLoaded, saveFrequency);
	}

}
