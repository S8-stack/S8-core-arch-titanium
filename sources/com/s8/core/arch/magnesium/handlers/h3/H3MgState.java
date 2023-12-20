package com.s8.core.arch.magnesium.handlers.h3;


/**
 * 
 */
public enum H3MgState {


	/**
	 * The resource has never been initialized (nothing on Disk)
	 * No unsaved modification (nothing on Disk, no resource set to the handler
	 */
	CREATED(
			false, /* is resource available ? */
			true /* is detachable ? */),


	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource is still NOT existing on the Disk.</li>
	 * <li>The resource has been initialized by the prgm.</li>
	 * </ul>
	 */
	INITIALIZED(
			true, /* is resource available ? */
			false /* is detachable ? */),


	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource is existing on the Disk.</li>
	 * <li>The resource is not yet loaded on the handler</li>
	 * </ul>
	 */
	UNMOUNTED(
			false, /* is resource available ? */
			true /* is detachable ? */), 



	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource is existing on the Disk.</li>
	 * <li>The resource has already been loaded</li>
	 * <li>Currently no deltas between cache and disk</li>
	 * </ul>
	 */
	SAVED(true, /* is resource available ? */
			true /* is detachable ? */), 



	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource MAY exist on the Disk.</li>
	 * <li>The resource has already been loaded</li>
	 * <li>Modifications did occured since loading</li>
	 * </ul>
	 */
	MODIFIED(true, /* is resource available */
			false /* is detachable ? */),


	/**
	 * the handler has failed and is now out of sync
	 */
	FAILED(false, /* is resource available */
			true /* is detachable ? */);



	public final boolean isResourceAvailable;
	public final boolean isDetachable;


	private H3MgState(
			boolean isResourceAvailable,
			boolean isDetachable) {
		this.isResourceAvailable = isResourceAvailable;
		this.isDetachable = isDetachable;
	}



}
