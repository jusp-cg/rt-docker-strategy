package com.capgroup.dcip.webapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.capgroup.dcip.app.ResourceNotFoundException;

import java.io.IOException;

/**
 * Centralized exception handler for the REST API
 */
@RestControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException exc, WebRequest request) {
		log.debug("Request failed with resource not found", exc);
		return new ResponseEntity<>("Resource Not Found: " + exc, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<String> handleIOException(IOException exc, WebRequest request) {
		return new ResponseEntity<String>("IOException: " + exc, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
