package com.s8.stack.arch.magnesium.stores.m3.nodes;

import java.io.IOException;

import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.stack.arch.magnesium.stores.m3.HashcodeModule;
import com.s8.stack.arch.magnesium.stores.m3.M3ModelPrototype;

/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class LinkM3Node<T> implements M3Node<T> {

	public @Override M3Node.Kind getKind() { return Kind.LINK; }

	
	/**
	 * 
	 */
	public final String key;
	
	/**
	 * 
	 */
	public long hashcode;
	
	/**
	 * 
	 */
	public T value;

	/**
	 * 
	 */
	public LinkM3Node<T> next;
	
	
	public LinkM3Node(String key) {
		super();
		this.key = key;
	}



	public void serialize(M3ModelPrototype<T> prototype, ByteOutflow outflow) throws IOException {
		outflow.putStringUTF8(key);
		prototype.serialize(value, outflow);
	}


	/**
	 * 
	 * @param <T>
	 * @param prototype
	 * @param inflow
	 * @return
	 * @throws IOException
	 */
	public static <T> LinkM3Node<T> deserialize(M3ModelPrototype<T> prototype, ByteInflow inflow) throws IOException {
		String key = inflow.getStringUTF8();
		LinkM3Node<T> link = new LinkM3Node<T>(key);
		link.hashcode = HashcodeModule.compute(key);
		link.value = prototype.deserialize(inflow);
		return link;
	}
}
