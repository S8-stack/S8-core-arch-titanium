package com.s8.arch.magnesium.core.paths;

import java.nio.charset.StandardCharsets;

public class MthAddress {

	/**
	 * 
	 * @param address
	 * @return
	 */
	public static byte[] hash(String address, int nBytes) {
		
		// find address bytes
		byte[] addressBytes = address.getBytes(StandardCharsets.UTF_8);
		
		// compute long hash
		int n = addressBytes.length;
		long hash = 0;
		for (int i=0; i<n; i++) {
			hash = 37 * hash + addressBytes[i];
		}
		
		// build hash bytes (least significant byte last)
		byte[] hashBytes = new byte[nBytes];
		for(int i=0; i<nBytes; i++) {
			hashBytes[nBytes-1-i] = (byte) (hash & 0xff);
			hash = hash >> 8;
		}
		
		return hashBytes;
	}

}
