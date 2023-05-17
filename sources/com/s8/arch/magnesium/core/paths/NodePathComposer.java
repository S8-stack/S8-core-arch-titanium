package com.s8.arch.magnesium.core.paths;

import java.nio.file.Path;


/**
 * 
 * @author pierreconvert
 *
 */
public class NodePathComposer {

	
	private final Path root;
	
	public final static String FILENAME = "node";
	
	public final static String EXTENSION = ".m2n";
	
	
	/**
	 * 
	 * @param root
	 * @param filename
	 * @param extension
	 */
	public NodePathComposer(Path root) {
		super();
		this.root = root;
	}
	
	

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Path compose(long id) {
		StringBuilder builder = new StringBuilder();
		
		int shift = 56, octet;
		for(int i=0; i<7; i++) {
			octet = (int) ((id>>shift) & 0xffL);
			builder.append('n');
			builder.append(String.format("%02x", octet & 0xff));
			builder.append('/');
			shift -= 8;
		}
		
		builder.append(FILENAME);
		builder.append('_');
		octet = (int) (id & 0xffL);
		builder.append(String.format("%02x", octet & 0xff));
		builder.append(EXTENSION);
		
		// root resolver
		return root.resolve(builder.toString());
	}

	
}
