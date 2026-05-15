package com.healthcare.command.exception;

import com.healthcare.command.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException exception){
        ApiResponse response = new ApiResponse(
                exception.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    // Generic exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(ResourceNotFoundException exception){
        ApiResponse response = new ApiResponse(
                "Something went wrong",
                null
        );
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Validation exception
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        String errorMsg = Objects.requireNonNull(ex.getBindingResult()
                        .getFieldError())
                .getDefaultMessage();

        ApiResponse response = new ApiResponse(errorMsg, null);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
