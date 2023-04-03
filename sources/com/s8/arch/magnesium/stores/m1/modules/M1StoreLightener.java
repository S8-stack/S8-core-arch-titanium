package com.s8.arch.magnesium.stores.m1.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import com.s8.arch.magnesium.callbacks.C0Callback;
import com.s8.arch.magnesium.callbacks.C0GroupCallback;
import com.s8.arch.magnesium.stores.m1.M1Handle;
import com.s8.arch.magnesium.stores.m1.M1Store;
import com.s8.arch.silicon.watch.WatchTask;



/**
 * <p>
 * Note that lightener cannot process block that are currently under loading.
 * This is not seen as a problem since:
 * </p>
 * <ul>
 * <li>Shell under loading are "hot block" (there is recent activity on them and
 * lightener is supposed to avoid them)</li>
 * <li>On the opposite, removable shells are "cold" ones (with no recent
 * activity)
 * </ul>
 * <p>
 * Size of memory is supposed to be consistent with the fact that the two
 * previously sets are (mostly) disjoint.
 * </p>
 * 
 * @author pc
 *
 */
public class M1StoreLightener<M> {

	/**
	 * 
	 * @author pierreconvert
	 *
	 * @param <M>
	 */
	private static class MetricsBuilder<M> implements BiConsumer<String, M1Handle<M>> {

		private long bytecount;

		private List<AccessMetric> metrics;

		public MetricsBuilder(List<AccessMetric> metrics) {
			super();
			this.metrics = metrics;
			bytecount=0;
		}

		@Override
		public void accept(String address, M1Handle<M> handler) {
			try {
				bytecount+=handler.getBytecount();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			metrics.add(new AccessMetric(address, handler));
		}

		public long getByteCount() {
			return bytecount;
		}
	}


	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	private static class AccessMetric implements Comparable<AccessMetric> {

		private final String address;

		private final long timestamp;

		private final long bytecount;


		public AccessMetric(String address, M1Handle<?> handler) {
			super();
			this.address = address;
			this.timestamp = handler.timestamp;
			long bytecount = 0;
			try {
				bytecount = handler.getBytecount();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			this.bytecount = bytecount;
		}

		@Override
		public int compareTo(AccessMetric right) {
			if(timestamp<right.timestamp) {
				return -1;
			}
			else if(timestamp>right.timestamp) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}



	public final static long START_CALL_COUNT = 0x02L;

	private M1Store<M>.Facet store;

	private long capacity;

	private final AtomicLong iCall;

	private final AtomicBoolean isBusy;

	private AtomicLong sequenceID;

	private boolean isVerbose;

	public static boolean DEBUG_forceVerbose = true;

	/**
	 * 
	 * @param store
	 * @param capacity
	 */
	public M1StoreLightener(M1Store<M>.Facet store, long capacity, boolean isVerbose) {
		super();
		this.store = store;
		this.capacity = capacity;
		iCall = new AtomicLong(START_CALL_COUNT);
		isBusy = new AtomicBoolean(false);

		sequenceID = new AtomicLong(0x00);

		this.isVerbose = isVerbose || DEBUG_forceVerbose;

	}



	/**
	 * 
	 */
	public void lighten() {


		// every 1024 calls ~(1000 x 8kB) ~ every 8MB changes in the RAM
		if((iCall.getAndIncrement() & 0x3ff) == 0 

				// ensure thread safety (not triggered two times or never) 
				&& isBusy.compareAndSet(false, true)) { 


			iCall.set(START_CALL_COUNT);


			store.getEngine().pushT2Task(new LightenTask(sequenceID.getAndIncrement()));	

		}


	}



	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	public class LightenTask implements WatchTask {

		/**
		 * For debugging purposes
		 */
		private long id;

		public LightenTask(long id) {
			super();
			this.id = id;
		}

		@Override
		public String describe() {
			return "MAP LIGHTENER (Sequence ID = "+id+")";
		}

		@Override
		public WatchTask run() {

		

			/* explore loaded shells */
			List<AccessMetric> metrics = new ArrayList<AccessMetric>();
			MetricsBuilder<M> builder = new MetricsBuilder<>(metrics);
			store.traverse(builder);

			/* in case bytecount is actually greater than allocated capacity target */
			long excess = builder.getByteCount() - capacity;
			
			if(excess>0) {

				if(isVerbose) {
					System.out.println("[M1StoreLightener] Starting clean-up sequence (ID = "+id+")");
					System.out.println("\texcess: "+excess);
				}
				
				Collections.sort(metrics);



				List<String> removables = new ArrayList<>();

				long cumulatedRemovablesBytecount = 0;
				int index =0, length=metrics.size();

				while(index< length &&  cumulatedRemovablesBytecount < excess) {
					AccessMetric metric = metrics.get(index);
					cumulatedRemovablesBytecount+=metric.bytecount;
					removables.add(metric.address);
					index++;
				}
				if(isVerbose) {
					System.out.println("[M1-Lightener] Removing "+removables.size()+" elements... (excess ="+excess+").");
				}
				dispose(removables);
			}
			else {
				// release lightener
				isBusy.set(false);
				
			}

			// no direct call again
			return null;
		}

		/**
		 * 
		 * @param removableSet
		 */
		private void dispose(List<String> removables) {

			int n = removables.size();

			C0GroupCallback callbackGrouping = new C0GroupCallback.FailFast(n, new C0Callback() {

				@Override
				public void onTerminated(int code) {
					isBusy.set(false);

					if(isVerbose) {
						System.out.println("[M1StoreLightener] "
								+ "Clean-up sequence (ID="+id+") "
								+ "terminated ("+n+" elements disposed)");
					}
				}
			});

			for(int i=0; i<n; i++) {
				/* <unit-disposal-op> */
				M1Handle<M> handler = store.getModelHandler0(removables.get(i));

				/* first shut-down the handler. Since it is a MthLocked<> object,
				 * il will continue accumulating requests (Callback)
				 */
				if(handler != null) {

					handler.requestShutDown(callbackGrouping.getItem(i));
				}
			}	
		}
	}


}
