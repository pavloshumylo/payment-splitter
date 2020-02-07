package com.eleks.handler;

import com.eleks.dto.ErrorResponseDto;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.exception.UserServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@ControllerAdvice
public class ErrorResponseExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(401, ex.getMessage(), LocalDate.now()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserServiceException.class)
    public final ResponseEntity<ErrorResponseDto> handleUserServiceException(UserServiceException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(500, ex.getMessage(), LocalDate.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public final ResponseEntity<ErrorResponseDto> handleInvalidRequestException(InvalidRequestException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(400, ex.getMessage(), LocalDate.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(404, ex.getMessage(), LocalDate.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(400, messageForMethodArgumentNotValidException(ex), LocalDate.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(400, ex.getMostSpecificCause().getMessage(), LocalDate.now()), HttpStatus.BAD_REQUEST);
    }

    private String messageForMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        List<String> validationErrorMessages = new LinkedList<>();

        ex.getBindingResult().getGlobalErrors()
                .forEach(error -> validationErrorMessages.add(error.getDefaultMessage()));

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationErrorMessages.add(String.format("%s", error.getDefaultMessage())));

        sb.append(String.join(", ", validationErrorMessages));
        sb.append(".");
        return sb.toString();
    }
}
