package com.s8.arch.magnesium.oldcallbacks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;


/**
 * 
 * @author pierreconvert
 *
 */
public abstract class C0GroupCallback {
	
	
	public final static int NOT_INITIATED = -1;
	
	
	public final static int ERROR = 2;
	
	
	public final int n;
	
	
	/**
	 * 3
	 * 
	 * Actually watching the implementation of AtomicIntegerArray
	 * 
	 * http://fuseyism.com/classpath/doc/java/util/concurrent/atomic/AtomicIntegerArray-source.html
	 * 
	 * it seem that it is managed with more attention then I thought.
	 * 
	 * It doesn't use Objects to store the values, making it more efficient in
	 * memory. In fact it uses a simple int[] and then access them in a safe way.
	 * 
	 * So I think that if you need to use many AtomicInteger it is better to use the
	 * AtomicIntegerArray.
	 * 
	 * AtomicIntegerArray: uses the Unsafe class to make atomic access to a single
	 * int[] in the AtomicIntegerArray
	 * 
	 * AtomicBoolean[]: every single object of the array has it's object(itself) for
	 * making atomic access
	 * 
	 * So I would expect a better performance in a heavy concurrent threaded
	 * environment with an AtomicBoolean[], with more memory consumption than the
	 * AtomicIntegerArray.
	 */
	private final AtomicIntegerArray terminationCodes;
	
	private final C0Callback groupCallback;
	
	private final AtomicInteger count;

	private final Item[] items;

	
	public C0GroupCallback(int n, C0Callback groupCallback) {
		super();
		this.n = n;
		this.terminationCodes = new AtomicIntegerArray(n);
		for(int i=0; i<n; i++) {
			terminationCodes.set(i, NOT_INITIATED);
		}
		this.groupCallback = groupCallback;
		this.count = new AtomicInteger(0);
		
		// build items
		items = new Item[n];
		for(int i=0; i<n; i++) {
			items[i] = new Item(i);
		}
		
	}
	
	
	
	public abstract int mergeCodes(AtomicIntegerArray codes);
	
	
	public C0Callback getItem(int i) {
		return items[i];
	}
	
	
	
	private class Item implements C0Callback {
		
		private final int index;
		
		public Item(int index) {
			super();
			this.index = index;
		}

		@Override
		public void onTerminated(int code) {

			terminationCodes.set(index, code);
		
			int c = count.incrementAndGet();
			if(c == n) {
				groupCallback.onTerminated(mergeCodes(terminationCodes));
			}
		}
	}
	
	
	
	public static class FailFast extends C0GroupCallback {

		public FailFast(int n, C0Callback groupCallback) {
			super(n, groupCallback);
		}
		
		@Override
		public int mergeCodes(AtomicIntegerArray codes) {
			for(int i=0; i<n; i++) {
				if(codes.get(i) != C0Callback.SUCCESSFUL) {
					return ERROR;
				}
			}
			return 0x00;
		}
		
	}

}
