package com.possible.mecash.exceptiion;


import com.possible.mecash.dto.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto<Object>>  handleResourceNotFoundException(ResourceNotFoundException ex) {
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(404)
                .responseMessage(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(404)
                .responseMessage(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    // Handle InvalidRequestException
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ResponseDto<Object>>  handleInvalidRequestException(InvalidRequestException ex) {
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(400)
                .responseMessage(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    // Handle InsufficientBalanceException
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ResponseDto<Object>>  handleInsufficientBalanceException(InsufficientBalanceException ex) {
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(400)
                .responseMessage(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    }

    // Handle MethodArgumentNotValidException for @Valid annotations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>>  handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(400)
                .responseMessage(ex.getMessage())
                .data(errors)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGeneralException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "An unexpected error occurred");
        errors.put("details", ex.getMessage());
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(400)
                .responseMessage(ex.getMessage())
                .data(errors)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

