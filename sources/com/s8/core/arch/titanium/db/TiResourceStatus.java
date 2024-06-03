package com.s8.core.arch.titanium.db;


/**
 * 
 */
public enum TiResourceStatus {
	
	/**
	 * Status is as followed:
	 * We don't know anything about resource (existing or not)
	 */
	UNDEFINED(0x02, "Resource might be loaded, but has not yet been"),
	
	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource is existing on the Disk.</li>
	 * <li>The resource has not yet been loaded</li>
	 * <li>Currently no deltas between cache and disk</li>
	 * </ul>
	 */
	UNMOUNTED(0x04, "Resource might be loaded, but has not yet been"),
	
	
	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource is existing on the Disk.</li>
	 * <li>The resource has already been loaded</li>
	 * <li>Currently no deltas between cache and disk</li>
	 * </ul>
	 */
	OK(0x24, "Resource is available"),

	
	
	/**
	 * The resource has been deleted unsafe to use
	 */
	DELETED(0x37, "Resource has been deleted from the disk"),
	
	
	/**
	 * The resource has never been initialized (nothing on Disk)
	 * No unsaved modification (nothing on Disk, no resource set to the handler
	 */
	NO_RESOURCE(0x52, "No resource has been found in the db"),



	/**
	 * 
	 */
	FAILED_TO_LOAD (0x53, "Resource has been found but loading has failed"),

	
	/**
	 * 
	 */
	FAILED_TO_SAVE(0x57, "Resource is live but failed to write back");

	
	
	
	
	/**
	 * 
	 */
	public final int code;
	
	
	/**
	 * 
	 */
	public final String message;

	
	/**
	 * 
	 * @param code
	 * @param message
	 */
	private TiResourceStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}
	

}
