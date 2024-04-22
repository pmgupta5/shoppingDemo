package com.lti.productservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<Object> handleSecurityException(ProductException ex){
        LOGGER.error("error: {}", (Object) ex.getStackTrace());
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", ex.getMessage());
        errorMap.put("status", HttpStatus.BAD_REQUEST.toString());
        return ResponseEntity.ok(errorMap);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException runtimeException){
        LOGGER.error("error: {}", (Object) runtimeException.getStackTrace());
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", runtimeException.getMessage());
        errorMap.put("status", HttpStatus.EXPECTATION_FAILED.toString());
        return ResponseEntity.ok(errorMap);
    }
}
