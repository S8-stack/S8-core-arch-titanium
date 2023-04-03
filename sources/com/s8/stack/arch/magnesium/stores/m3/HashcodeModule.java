package com.s8.stack.arch.magnesium.stores.m3;

import java.nio.charset.StandardCharsets;

public class HashcodeModule {

	/**
	 * 64 prime numbers following 31 (the one used for hashing in Java native
	 * hashcode() method).
	 */
	public final static long[] PRIMES = new long[] { 
			31, 37, 41, 43, 47, 53, 59, 61, // group 0
			3581, 3583, 3593, 3607, 3613, 3617, 3623, 3631, // group 1
			3637, 3643, 3659, 3671, 3673, 3677, 3691, 3697, // group 2
			67, 71, 73, 79, 83, 89, 97, 101, // group 3
			149, 151, 157, 163, 167, 173, 179, 181, // group 4
			2357, 2371, 2377, 2381, 2383, 2389, 2393, 2399,	// group 5
			233, 239, 241, 251, 257, 263, 269, 271, // group 6
			277, 281, 283, 293, 307, 311, 313, 317 // group 7
	}; 

	
	public static long compute(String s) {

		/*
		 * (From Java source v11) public static int hashCode(byte[] value) { int h = 0;
		 * for (byte v : value) { h = 31 * h + (v & 0xff); } return h; }
		 */

		byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);
		int length = bytes.length;
		long hashcode = 0L;
		for (int i = 0; i < length; i++) {
			hashcode = PRIMES[i % 64] * hashcode + (bytes[i] & 0xff);
		}
		return hashcode;
	}
	
	



	public static long powerOf2(int exponent) {
		long p = 1;
		for(int i=0; i<exponent; i++) {
			p*=2;
		}
		return p;
	}
	
	
	public static int generateMask(int exponent) {
		int mask = 0;
		for(int i=0; i<exponent; i++) {
			mask = (mask << 1) | 0x1;
		}
		return mask;
	}
	


	public static String printMask(long mask) {
		StringBuilder builder = new StringBuilder();
		String rawBinRep = Long.toBinaryString(mask);
		int n = rawBinRep.length();
		int p = n/8;
		int leading = n%8;
		builder.append(rawBinRep.substring(0, leading));
		for(int i=0; i<p; i++) {
			builder.append('|');
			builder.append(rawBinRep.substring(leading+i*8, leading+(i+1)*8));
		}
		return builder.toString();
	}
	

}
