package com.daw.services.exceptions;

public class TareaNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7588639429307040316L;

	public TareaNotFoundException(String message) {
		super(message);
	}
	
}
