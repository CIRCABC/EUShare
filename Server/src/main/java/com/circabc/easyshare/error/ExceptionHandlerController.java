/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.error;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

   @ExceptionHandler(value = ResponseStatusException.class)
   public ResponseEntity<Object> responseStatusException(ResponseStatusException exception) {
      return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
   }

  
   @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
   public ResponseEntity<Object> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
      log.error(exception.getMessage(),exception);
      return new ResponseEntity<>(HttpErrorAnswerBuilder.build500MediaTypeNotSupportedToString(),
            HttpStatus.INTERNAL_SERVER_ERROR);
   }

   @ExceptionHandler(value = org.springframework.http.converter.HttpMessageNotReadableException.class)
   public ResponseEntity<Object> httpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException exception) {
      return new ResponseEntity<>(HttpErrorAnswerBuilder.build400EmptyToString(),
            HttpStatus.BAD_REQUEST);
   }

   @ExceptionHandler(value = ConstraintViolationException.class)
   public ResponseEntity<Object> httpMessageNotReadableException(ConstraintViolationException exception) {
      return new ResponseEntity<>(HttpErrorAnswerBuilder.build400EmptyToString(),HttpStatus.BAD_REQUEST);
   }

   @ExceptionHandler(value = RuntimeException.class)
   public ResponseEntity<Object> nullPointerException(RuntimeException exception) {
      log.error(exception.getMessage(),exception);
      return new ResponseEntity<>(HttpErrorAnswerBuilder.build500EmptyToString(), HttpStatus.INTERNAL_SERVER_ERROR);
   }

   @ExceptionHandler(value = Exception.class)
   public ResponseEntity<Object> handle(Exception ex) {
      log.error(ex.getMessage(),ex);
      return new ResponseEntity<>(HttpErrorAnswerBuilder.build500EmptyToString(), HttpStatus.INTERNAL_SERVER_ERROR);
   }
   
}