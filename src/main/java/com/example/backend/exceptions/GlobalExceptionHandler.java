package com.example.backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        List<String> messages = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                messages.add(error.getDefaultMessage()));
        errors.put("messages", messages);
        log.error("Validations errors: {}", messages);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}