package com.wendelnunes.assembleia.api.controllers;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.wendelnunes.assembleia.api.dtos.ErrorResponseDTO;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotDeleteException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //
				.body(createErrorResponse("Server Error", //
						Collections.singletonList(ex.getLocalizedMessage()), //
						request)); //
	}

	@ExceptionHandler(ConflictException.class)
	public final ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT) //
				.body(createErrorResponse(ex.getLocalizedMessage(), //
						request)); //
	}

	@ExceptionHandler(DateTimeException.class)
	public final ResponseEntity<ErrorResponseDTO> handleDateTimeException(DateTimeException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse(ex.getLocalizedMessage(), //
						request)); //
	}

	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND) //
				.body(createErrorResponse(ex.getLocalizedMessage(), //
						ex.getDetails(), //
						request)); //
	}

	@ExceptionHandler(NotDeleteException.class)
	public final ResponseEntity<ErrorResponseDTO> handleNotDeleteException(NotDeleteException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse(ex.getLocalizedMessage(), //
						request)); //
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> details = ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse("Erro de Validação", //
						details, //
						((ServletWebRequest) request).getRequest())); //
	}

	private static ErrorResponseDTO createErrorResponse(String message, List<String> details,
			HttpServletRequest request) {
		return ErrorResponseDTO.builder() //
				.message(message) //
				.details(details) //
				.timestamp(OffsetDateTime.now()) //
				.path(request.getRequestURI()) //
				.build();
	}

	private static ErrorResponseDTO createErrorResponse(String message, HttpServletRequest request) {
		return createErrorResponse(message, null, request);
	}

}
