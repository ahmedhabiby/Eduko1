package com.springboot.eduko.handleExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class CustomException {
    private final BundleService bundleService;

    @Autowired
    public CustomException(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(bundleService.getResponse(e.getMessage()));

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Response>> handleException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getBindingResult().getFieldErrors().stream().map(error -> bundleService.getResponse(error.getDefaultMessage())).toList());
    }
}
