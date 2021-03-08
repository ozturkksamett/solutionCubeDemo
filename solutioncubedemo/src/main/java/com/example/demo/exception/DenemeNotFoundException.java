package com.example.demo.exception;

public class DenemeNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DenemeNotFoundException(Long id) {
	    super("Could not find employee " + id);
	  }
}
