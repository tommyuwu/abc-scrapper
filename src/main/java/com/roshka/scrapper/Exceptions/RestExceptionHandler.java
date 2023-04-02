package com.roshka.scrapper.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.roshka.scrapper.Exceptions.CustomExceptions.*;

import java.util.*;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("codigo", "g267");
        errorMap.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("codigo", "g268");
        errorMap.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<?> handleInternalServerException(InternalServerException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("codigo", "g100");
        errorMap.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
    }
}

