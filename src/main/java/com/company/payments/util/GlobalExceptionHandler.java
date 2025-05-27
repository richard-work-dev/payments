package com.company.payments.util;

import com.company.payments.constant.ErrorsMessageEnum;
import com.company.payments.data.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) throws JsonProcessingException {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.append(error.getDefaultMessage()).append(". ");
        });
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(String.valueOf(errors));
        errorResponse.setErrorCode(ErrorsMessageEnum.PAYMENT_CREATED_ERROR.getCode());
        ObjectMapper objectMapper = new ObjectMapper();
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(errorResponse));
    }
}
