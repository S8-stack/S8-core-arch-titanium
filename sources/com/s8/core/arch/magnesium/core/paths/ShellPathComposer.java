package com.s8.core.arch.magnesium.core.paths;

import java.nio.file.Path;


public class ShellPathComposer {

	public static final String DB_DEFAULT_PATHNAME = "blocks-db";


	public final static String NODE_PREFIX = "n";

	public final static String BLOCK_PREFIX = "b";

	public final static String FOLDER_SEPARATOR = "/";


	
	private final Path root;
	
	private final int depth;
	
	private final String extension;
	
	public ShellPathComposer(Path root, int depth, String extension) {
		super();
		this.root = root;
		this.depth = depth;
		this.extension = extension;
	}
	
	
	/**
	 * 
	 * @param root
	 * @return the Path object computed from the root passed as argument
	 */
	public Path getPath(String address) {
		byte[] hash = MthAddress.hash(address, depth);

		// concatenate first 8 bytes into repo
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i<depth; i++){
			builder.append(NODE_PREFIX);
			builder.append(String.format("%02x", hash[i] & 0xff));
			builder.append(FOLDER_SEPARATOR);
		}
		
		builder.append(address);
		
		builder.append(extension);

		// root resolver
		return root.resolve(builder.toString());
	}
	
	/*
	public Path getPath(String address, String filename) {
		byte[] hash = MthAddress.hash(address);

		// concatenate first 8 bytes into repo
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i<depth; i++){
			builder.append(NODE_PREFIX);
			builder.append(String.format("%02x", hash[i] & 0xff));
			builder.append(FOLDER_SEPARATOR);
		}
		
		// append filename
		builder.append(filename);

		// root resolver
		return root.resolve(builder.toString());
	}
	*/
	
	
	
	
	/**
	 * <ul>
	 * <li>1 file: /filename_0.ext</li>
	 * <li>2 files: /filename_1-${hexacode in one digit}.ext</li>
	 * <li>4 files: /filename_2-${00, 01, 10, 11}.ext</li>
	 * <li>8 files: /filename_3-${000, 001, etc., 111}.ext</li>
	 * <li>2^4 files: /filename_4_${hexacode in one digit}.ext</li>
	 * <li>2^5 files: /filename_5-${hexacode in two digits}.ext</li>
	 * <li>2^8=256 files: /filename_8-${index in hexadecimal two digits}.ext</li>
	 * <li>2^9=2*0xff files: long code (9 bits available): 1 bit(->folder structure)
	 * + 8bits (-> filename) /s-{index in hexadecimal two digits}/filename_1-${index
	 * in hexadecimal two digits}.ext</li>
	 * <li>2^10=2*0xff files: long code (10 bits available): 2 bit(->folder
	 * structure) + 8bits (-> filename) /s-{index in hexadecimal two
	 * digits}/filename_2-${index in hexadecimal two digits}.ext</li>
	 * <li>2^16=2^8*0xff files: long code (16 bits available): 8 bit(->folder
	 * structure) + 8bits (-> filename) /s-{index in hexadecimal two
	 * digits}/filename_8-${index in hexadecimal two digits}.ext</li>
	 * <li>2^17=2^1*0xff*0xff files: long code (17 bits available): 1 bits (folder
	 * struct level0)/ B1 = 8bits (folder structure level 1) + 8 bits = B0 (->
	 * filename) /s-{index in hexadecimal two digits}/s-{index in hexadecimal two
	 * digits of B1}/filename_1-${index in hexadecimal two digits of B0}.ext</li>
	 * </ul>
	 * @return 
	 */
	public Path composeSerieElementPath(Path root, long hashcode, int nbits, String filename, String extension) {
		StringBuilder builder = new StringBuilder();
		
		int folderDepth = 0, fileLevel = 0;
		if(nbits>0) {
			folderDepth = (nbits-1)/8;
			fileLevel = (nbits-1)%8 + 1;	
		}
		
		
		int[] octets = new int[folderDepth+1];
		int shift = 0;
		for(int i=0; i<folderDepth+1; i++) {
			octets[i] = (int) ((hashcode>>shift) & 0xffL);
			shift += 8;
		}
		
		for(int depth=0; depth<folderDepth; depth++) {
			builder.append('s');
			builder.append(String.format("%02x", octets[folderDepth-1-depth] & 0xff));
			builder.append('/');
		}
		
		builder.append(filename);
		builder.append('_');
		builder.append(Integer.toString(fileLevel));
		builder.append('-');
		builder.append(String.format("%02x", octets[0]));
		builder.append(extension);
		
		// root resolver
		return root.resolve(builder.toString());
		
	}
	
	
	
	public static byte[] readBytesFromLong(long value) {
		byte[] bytes = new byte[8];

		// least significant bytes first
		for(int i=0; i<8; i++) {
			bytes[i] = (byte) (0xff & value);
			value >>= 8;
		}
		return bytes;
	}
}
