package com.s8.arch.magnesium.stores.m2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.s8.arch.magnesium.paths.NodePathComposer;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;

public class M2Store<K> {

	
	private static final byte[] OPENING_TAG = "<store:>".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] CLOSING_TAG = "</store>".getBytes(StandardCharsets.US_ASCII);

	public final NodePathComposer pathComposer;
	
	protected final AtomicLong nodeIdGenerator;
	
	public final M2Prototype<K> prototype;
	
	public M2Node<K> head;
	
	public final int dimension;
	
	public final Path path;
	
	public boolean isVerbose;
	
	
	public M2Store(M2Prototype<K> prototype, Path root, int dimension, boolean isVerbose) {
		super();
		this.prototype = prototype;
		this.pathComposer = new NodePathComposer(root);
		path = root.resolve("store");
		this.nodeIdGenerator = new AtomicLong();
		this.dimension = dimension;
		
		this.isVerbose = isVerbose;
	}
	
	

	
	/**
	 * 
	 * @return
	 */
	public LeafM2Node<K> createNewLeafNode(long index0) {
		long id = nodeIdGenerator.getAndIncrement();
		if(isVerbose) {
			System.out.println("New ID: "+id);	
		}
		Path path = pathComposer.compose(id);
		
		LeafM2Node<K> node = new LeafM2Node<K>(id, path, index0);
		
		node.init(prototype.getRawType(), dimension);
		
		return node;
	}
	
	
	public ForkM2Node<K> createNewForkNode(M2Node<K> first) {
		long id = nodeIdGenerator.getAndIncrement();
		if(isVerbose) {
			System.out.println("New ID: "+id);	
		}
		
		Path path = pathComposer.compose(id);
		ForkM2Node<K> node = new ForkM2Node<K>(id, path, first.getDepth()+1, first.index0, first.getIndexDelta()*dimension);
		
		node.init(this, first);
		
		return node;
	}
	
	/**
	 * 
	 * @param value
	 * @throws IOException 
	 */
	public void add(K value) throws IOException {
		
		if(head.isFull(this)) {
			head = createNewForkNode(head);
		}
		
		M2Node<K> node = head;
		while(node != null) {
			node = node.add(this, value);
		}
	}
	
	
	public K get(long index) {
		M2Node<K> node = head;
		while(!node.isLeaf()) {
			node = node.getSubNode(index);
		}
		return node.get(index);
	}
	
	
	public void traverse(Consumer<K> consumer) throws IOException {
		head.traverse(this, consumer);
	}
	
	
	
	/**
	 * 
	 */
	public void initialize(long index0) {
		head = createNewLeafNode(index0);
		nodeIdGenerator.set(0x02L);
	}
	

	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void deserialize(ByteInflow inflow) throws IOException {
		if(!inflow.matches(OPENING_TAG)) {
			throw new IOException("Opneing sequence not matching");
		}
		long freeId = inflow.getInt64();
		nodeIdGenerator.set(freeId);
		
		int depth = inflow.getUInt8();
		long index0 = inflow.getInt64();
		long indexDelta = inflow.getInt64();
		
		switch(inflow.getUInt8()) {
		
		case M2Node.LEAF_CODE : 
			head = LeafM2Node.deserializeLeafHeader(this, inflow, depth, index0, indexDelta);
			break;
			
		case M2Node.FORK_CODE : 
			head = ForkM2Node.deserializeForkHeader(this, inflow, depth, index0, indexDelta);
			break;
			
		default : throw new IOException("No mathing code");
		}

		if(!inflow.matches(CLOSING_TAG)) {
			throw new IOException("Clsosing sequence not matching");
		}
	}
	
	
	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public void serialize(ByteOutflow outflow) throws IOException {
		outflow.putByteArray(OPENING_TAG);
		outflow.putInt64(nodeIdGenerator.get());
		
		// one time header maths
		outflow.putUInt8(head.getDepth());
		outflow.putInt64(head.index0);
		outflow.putInt64(head.getIndexDelta());
		
		head.serializeHeader(outflow);
		
		outflow.putByteArray(CLOSING_TAG);	
	}
	
}
