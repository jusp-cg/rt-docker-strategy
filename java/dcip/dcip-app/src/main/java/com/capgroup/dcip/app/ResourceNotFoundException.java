package com.capgroup.dcip.app;

/**
 * Exception that represents an entity/resource not found 
 */
public class ResourceNotFoundException extends RuntimeException {
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 6004551803219558843L;

	public ResourceNotFoundException(String resourceType, String resourceId) {
		super("Type:" + resourceType + " id:" + resourceId);
	}

	public ResourceNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ResourceNotFoundException(String arg0) {
		super(arg0);
	}

	public ResourceNotFoundException(Throwable arg0) {
		super(arg0);
	}
}
