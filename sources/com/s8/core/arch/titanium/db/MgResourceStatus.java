package com.s8.core.arch.titanium.db;


/**
 * 
 */
public class MgResourceStatus {
	
	
	
	public final static int OK_CODE = 0x02;
	
	

	/**
	 * Status is as followed:
	 * <ul>
	 * <li>Resource is existing on the Disk.</li>
	 * <li>The resource has already been loaded</li>
	 * <li>Currently no deltas between cache and disk</li>
	 * </ul>
	 */
	public final static MgResourceStatus OK = new MgResourceStatus(OK_CODE, "Resource is available");

	
	
	/**
	 * The resource has been deleted unsafe to use
	 */
	public final static MgResourceStatus DELETED = new MgResourceStatus(0x17, "Resource has been deleted from the disk");
	
	
	/**
	 * The resource has never been initialized (nothing on Disk)
	 * No unsaved modification (nothing on Disk, no resource set to the handler
	 */
	public final static MgResourceStatus NO_RESOURCE_IN_DB = new MgResourceStatus(0x42, "No resource has been found in the db");



	/**
	 * 
	 */
	public final static MgResourceStatus FAILED_TO_LOAD = new MgResourceStatus(0x52, "Resource has been found but loading has failed");

	
	/**
	 * 
	 */
	public final static MgResourceStatus FAILED_TO_SAVE = new MgResourceStatus(0x53, "Resource is live but failed to write back");

	
	
	
	
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
	public MgResourceStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	
	public boolean isAvailable() {
		return code == OK_CODE;
	}


}
