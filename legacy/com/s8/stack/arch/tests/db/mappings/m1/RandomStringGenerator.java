package com.s8.stack.arch.tests.db.mappings.m1;

public class RandomStringGenerator {
	
	public final static char[] CHARS = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x',
			'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'@', '#', '-', '_', '.', '$'
	};
	
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String generate() {
		int range = CHARS.length;
		int length = (int) (Math.random()*64)+8;
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<length; i++) {
			builder.append(CHARS[(int) (Math.random() * range)]);
		}
		return builder.toString();
	}

}
