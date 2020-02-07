package com.eleks.handler;

import com.eleks.dto.ErrorResponseDto;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.exception.UserServiceException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErrorResponseExceptionHandlerTest {

    private ErrorResponseExceptionHandler errorResponseExceptionHandler = new ErrorResponseExceptionHandler();

    @Test
    public void handleResourceNotFoundException_shouldReturnProperResponseEntityWithNotFoundStatus() {
        ResponseEntity<ErrorResponseDto> responseEntityActual =
                errorResponseExceptionHandler.handleResourceNotFoundException(new ResourceNotFoundException("testMessage"));

        compareResponses(new ResponseEntity<>(new ErrorResponseDto(404, "testMessage",
                LocalDate.of(2010, 1, 1)), HttpStatus.NOT_FOUND), responseEntityActual);
    }

    @Test
    public void handleInvalidUserDtoException_shouldReturnProperResponseEntityWithBadRequestStatus() {
        ResponseEntity<ErrorResponseDto> responseEntityActual =
                errorResponseExceptionHandler.handleInvalidRequestException(new InvalidRequestException("testMessage"));

        compareResponses(new ResponseEntity<>(new ErrorResponseDto(400, "testMessage",
                LocalDate.of(2010, 1, 1)), HttpStatus.BAD_REQUEST), responseEntityActual);
    }

    @Test
    public void handleUserServiceException_shouldReturnProperResponseEntityWithInternalServerErrorStatus() {
        ResponseEntity<ErrorResponseDto> responseEntityActual =
                errorResponseExceptionHandler.handleUserServiceException(new UserServiceException("testMessage"));

        compareResponses(new ResponseEntity<>(new ErrorResponseDto(500, "testMessage",
                LocalDate.of(2010, 1, 1)), HttpStatus.INTERNAL_SERVER_ERROR), responseEntityActual);
    }

    private void compareResponses(ResponseEntity<ErrorResponseDto> responseEntityExpected, ResponseEntity<ErrorResponseDto> responseEntityActual) {
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
        assertEquals(responseEntityExpected.getBody().getMessage(), responseEntityActual.getBody().getMessage());
        assertEquals(responseEntityExpected.getBody().getStatusCode(), responseEntityActual.getBody().getStatusCode());
        assertNotNull(responseEntityActual.getBody().getTimestamp());
    }
}
