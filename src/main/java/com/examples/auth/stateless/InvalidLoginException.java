package com.examples.auth.stateless;

public class InvalidLoginException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidLoginException(String message) {
		super(message);
	}

	public InvalidLoginException(String message, Throwable t) {
		super(message, t);
	}
}
