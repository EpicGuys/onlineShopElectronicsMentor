package com.EpicGuys.EpicShop.exceptions;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdvisorController {
	@ExceptionHandler(RegistrationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> validationExceptionHandler(RegistrationException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}
}
