package com.wendelnunes.assembleia.exceptions;

public class BadRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7931401865380153465L;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException() {
		super();
	}
}