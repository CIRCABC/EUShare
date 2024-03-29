/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.error;

import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlerController {

  private Logger log = LoggerFactory.getLogger(
      ExceptionHandlerController.class);

  @ExceptionHandler(value = ResponseStatusException.class)
  public ResponseEntity<Object> responseStatusException(
      ResponseStatusException exception) {
    if (exception.getRawStatusCode() != HttpStatus.NOT_FOUND.value())
      log.error(exception.getMessage(), exception);
    
    return new ResponseEntity<>(exception.getReason(), exception.getStatus());
  }

  @ExceptionHandler(value = MissingServletRequestPartException.class)
  public ResponseEntity<Object> missingServletRequestPartException(
      MissingServletRequestPartException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = MultipartException.class)
  public ResponseEntity<Object> multipartException(
      MultipartException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = MissingServletRequestParameterException.class)
  public ResponseEntity<Object> missingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> methodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Object> httpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<Object> httpMediaTypeNotAcceptableException(
      HttpMediaTypeNotAcceptableException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public ResponseEntity<Object> illegalArgumentException(
      IllegalArgumentException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<Object> httpMessageNotReadableException(
      org.springframework.http.converter.HttpMessageNotReadableException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = ConstraintViolationException.class)
  public ResponseEntity<Object> constraintViolationException(
      ConstraintViolationException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build400EmptyToString(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = RuntimeException.class)
  public ResponseEntity<Object> nullPointerException(
      RuntimeException exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build500EmptyToString(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Object> handle(Exception exception) {
    log.error(exception.getMessage(), exception);
    return new ResponseEntity<>(
        HttpErrorAnswerBuilder.build500EmptyToString(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
