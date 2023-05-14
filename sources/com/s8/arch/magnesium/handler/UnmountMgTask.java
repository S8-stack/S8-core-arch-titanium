package com.s8.arch.magnesium.handler;

import java.util.ArrayList;
import java.util.List;

import com.s8.arch.magnesium.callbacks.BooleanMgCallback;
import com.s8.arch.silicon.async.AsyncTask;
import com.s8.arch.silicon.async.MthProfile;

public class UnmountMgTask<R> implements AsyncTask {




	/**
	 * 
	 */
	public final MgSharedResourceHandler<R> handler;

	public final R resource;

	public final long cutOffTimestamp;

	public final BooleanMgCallback onUnmounted;


	/**
	 * 
	 * @param resource
	 * @param callback
	 */
	public UnmountMgTask(MgSharedResourceHandler<R> handler, R resource, long cutOffTimestamp, BooleanMgCallback onUnmounted) {
		super();
		this.handler = handler;
		this.resource = resource;
		this.cutOffTimestamp = cutOffTimestamp;
		this.onUnmounted = onUnmounted;
	}



	@Override
	public void run() {

		/**
		 * run callback on resource
		 */

		try {
			
			List<MgUnmountable> unmountables = new ArrayList<>();
			handler.getSubUnmountables(unmountables);
			
			Unmounter unmounter = new Unmounter(unmountables, onUnmounted);
			
			unmounter.unmount(cutOffTimestamp);
			
			
			/* save resource */
			/* save resource */
			if(!handler.isSaved()) {

				handler.getIOModule().save(resource);

				handler.notifySuccessfullySaved();
			}


			/* activate callback */
			unmounter.getLastSubCallback().call(handler.isDetachable(cutOffTimestamp));
		}
		catch (Exception e) {
			e.printStackTrace();

			handler.setFailed(e);
		}


	}



	@Override
	public String describe() {
		return "Unmounting resource for handler: "+handler.getName();
	}



	@Override
	public MthProfile profile() {
		return MthProfile.IO_SSD;
	}



	private class Unmounter {

		private final List<MgUnmountable> unmountables;

		private final int n;

		private final boolean[] hasSubCalledBack;

		private final boolean[] isSubDetachable;

		private final BooleanMgCallback[] subCallbacks;


		private final Object lock = new Object();

		public Unmounter(List<MgUnmountable> unmountables, BooleanMgCallback onUnmounted) {
			this.unmountables = unmountables;
			this.n = unmountables.size();

			hasSubCalledBack = new boolean[n + 1];

			isSubDetachable = new boolean[n + 1];

			subCallbacks = new BooleanMgCallback[n + 1];

			for(int i = 0; i < n+1; i++) {
				final int index = i;
				subCallbacks[i] = new BooleanMgCallback() {

					@Override
					public void call(boolean isDetachable) {
						synchronized (lock) {

							hasSubCalledBack[index] = true;
							isSubDetachable[index] = isDetachable;

							if(haveAllSubCalledBack()) {
								
								// finally unmount
								handler.setUnmounted();
								
								onUnmounted.call(areAllDetachable());
								
								/**
								 * Release handler for processing next operation
								 * ONLY AFTER callback is call (and subsequent action performed)
								 */
								handler.roll(true);
							}
						}
					}
				};
			}
		}

		public BooleanMgCallback getLastSubCallback() {
			return subCallbacks[n];
		}

		public void unmount(long cutOffTimestamp) {
			for(int i = 0; i < n; i++) {
				unmountables.get(i).unmount(cutOffTimestamp, subCallbacks[i]);
			}
		}




		/**
		 * 
		 * @return
		 */
		private boolean areAllDetachable() {
			for(int i = 0; i <= n; i++) {
				if(!isSubDetachable[i]) { 
					return false; 
				} 
			}
			return true;		
		}


		/**
		 * 
		 * @return
		 */
		private boolean haveAllSubCalledBack() {
			for(int i = 0; i <= n; i++) {
				if(!hasSubCalledBack[i]) { 
					return false; 
				} 
			}
			return true;		
		}
	}

}
