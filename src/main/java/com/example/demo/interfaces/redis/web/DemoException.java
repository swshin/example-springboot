package com.example.demo.interfaces.redis.web;

import org.springframework.http.HttpStatus;

public class DemoException extends Exception {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 4375253207174855L;
	
	private final HttpStatus httpStatus;

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public DemoException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}
}
