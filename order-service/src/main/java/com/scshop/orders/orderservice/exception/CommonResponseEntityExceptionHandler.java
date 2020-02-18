package com.scshop.orders.orderservice.exception;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;;

@ControllerAdvice
@RestController
public class CommonResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(OrderDetailsInvalidException.class)
	public final ResponseEntity<Object> handleOrderDetailsInvalidException(OrderDetailsInvalidException ex,
			WebRequest request) {
		
		ExceptionMessage exceptionResponse = new ExceptionMessage(HttpStatus.BAD_REQUEST, new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, new Date(),
				ex.getBindingResult().toString(), request.getDescription(false));

		return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		
		logger.error(ex.getStackTrace());
		
		ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, new Date(),
				ex.getLocalizedMessage(), request.getDescription(false));

		return new ResponseEntity<>(exceptionMessage, exceptionMessage.getStatus());
	}

}
