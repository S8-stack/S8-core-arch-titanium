package com.s8.stack.arch.magnesium.stores.m3;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.s8.stack.arch.magnesium.stores.m3.nodes.ForkM3Node;



/**
 * 
 * @author pierreconvert
 *
 * @param <T>
 */
public class ActiveList<T> {


	public static boolean DEBUG_showDisposalRun = false;
	public static boolean DEBUG_isVerbose = false;
	
	
	public class NodeHandler implements Comparable<NodeHandler> {
		
		public final ForkM3Node<T> node;
		
		public double relevance;
		
		public NodeHandler(ForkM3Node<T> node) {
			super();
			this.node = node;
		}
		
		
		public void signalActivity() {
			relevance += 1.0;
		}


		@Override
		public int compareTo(NodeHandler right) {
			/*
			 * Most relevant first, least relevant last...
			 */
			if(relevance < right.relevance) {
				return 1;
			}
			else if(relevance > right.relevance){
				return -1;
			}
			else {
				return 0;
			}
		}
	
		
	}
	
	
	private final M3Store<T> store;
	
	private int count = 0;
	
	private int persistencyCount = 0;
	private int saveFrequency;
	
	private int half;
	private int threshold;
	
	private int position;
	
	
	/**
	 * Most relevant first, least relevant last...
	 */
	private NodeHandler[] handlers;
	
	
	@SuppressWarnings("unchecked")
	public ActiveList(M3Store<T> store, int maxNbLoaded, int saveFrequency) {
		super();
		
		this.store = store;
		half = maxNbLoaded/2;
		threshold = maxNbLoaded/4*3;
		this.saveFrequency = saveFrequency;
		
		handlers = (ActiveList<T>.NodeHandler[]) Array.newInstance(NodeHandler.class, maxNbLoaded);
		this.position = 0;
	}
	
	
	
	public void dim() {
		if(++count == 8) {
			count = 0;
			NodeHandler handler;
			for(int i=0; i<position; i++) {
				handler = handlers[i];
				if(handler !=null) {
					handler.relevance *= 0.95;	
				}
			}
		}
	}
	
	
	
	public NodeHandler record(ForkM3Node<T> forkNode) {
		NodeHandler handler = new NodeHandler(forkNode);
		handlers[position++] = handler;
		return handler;
	}
	
	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void rollOver() throws IOException {
		
		/* 
		 * trim already saved if necessary
		 */
		if(position >= threshold) {
			if(DEBUG_showDisposalRun) {
				System.out.println("Disposing...");
			}
			Arrays.sort(handlers, 0, position);
			Queue<ForkM3Node<T>> queue = new LinkedList<>();
			for(int i=half; i<position; i++) {
				handlers[i].node.dipose(store, queue, DEBUG_isVerbose);
			}
			
			// in turns, dispose all subnodes
			while(!queue.isEmpty()) {
				queue.poll().dipose(store, queue, DEBUG_isVerbose);
			}
			
			int i2 = 0;
			NodeHandler handler;
			for(int i=0; i<half; i++) {
				if((handler = handlers[i]).node.isLoaded()) {
					handlers[i2++] = handler;
				}
			}
			position = i2;
			int length = handlers.length;
			for(int i=position; i<length; i++) {
				handlers[i] = null;
			}
		}
	}


	/**
	 * 
	 * @throws IOException
	 */
	public void persist() throws IOException {
		if(persistencyCount++ >= saveFrequency) {
			saveNodes();
			persistencyCount = 0;
		}
	}
	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void saveNodes() throws IOException {
		for(int i=0; i<position; i++) {
			handlers[i].node.save(store);
		}	
	}
	

	public int getMaxNbLoaded() {
		return handlers.length;
	}



	public int getSaveFrequency() {
		return saveFrequency;
	}
	
	
}
