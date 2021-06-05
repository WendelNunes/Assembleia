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
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotDeleteException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //
				.body(createErrorResponse(ex, //
						"Server Error", //
						Collections.singletonList(ex.getLocalizedMessage()), //
						request)); //
	}

	@ExceptionHandler(ConflictException.class)
	public final ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT) //
				.body(createErrorResponse(ex, //
						request)); //
	}

	@ExceptionHandler(DateTimeException.class)
	public final ResponseEntity<ErrorResponseDTO> handleDateTimeException(DateTimeException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse(ex, //
						request)); //
	}

	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND) //
				.body(createErrorResponse(ex, //
						ex.getDetails(), //
						request)); //
	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<ErrorResponseDTO> handleNotFoundException(BadRequestException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse(ex, //
						request)); //
	}

	@ExceptionHandler(NotDeleteException.class)
	public final ResponseEntity<ErrorResponseDTO> handleNotDeleteException(NotDeleteException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse(ex, //
						request)); //
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> details = ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST) //
				.body(createErrorResponse(ex, //
						"Erro de validação", //
						details, //
						((ServletWebRequest) request).getRequest())); //
	}

	private static ErrorResponseDTO createErrorResponse(Throwable throwable, String message, List<String> details,
			HttpServletRequest request) {
		log.error("Error: " + throwable.getMessage(), throwable);
		return ErrorResponseDTO.builder() //
				.message(message != null ? message : throwable.getMessage()) //
				.details(details) //
				.timestamp(OffsetDateTime.now()) //
				.path(request.getRequestURI()) //
				.build();
	}

	private static ErrorResponseDTO createErrorResponse(Throwable throwable, List<String> details,
			HttpServletRequest request) {
		return createErrorResponse(throwable, null, null, request);
	}

	private static ErrorResponseDTO createErrorResponse(Throwable throwable, HttpServletRequest request) {
		return createErrorResponse(throwable, null, null, request);
	}
}
