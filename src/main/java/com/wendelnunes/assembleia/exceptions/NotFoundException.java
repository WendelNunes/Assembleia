package com.wendelnunes.assembleia.exceptions;

import java.util.List;

import lombok.Getter;

@Getter
public class NotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 921227015279453101L;
	private List<String> details;

	public NotFoundException(String msg, List<String> details) {
		super(msg);
		this.details = details;
	}

	public NotFoundException(String msg) {
		super(msg);
	}

	public NotFoundException() {
		super();
	}
}
