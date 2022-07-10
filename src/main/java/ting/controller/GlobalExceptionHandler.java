package ting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ting.dto.ResponseError;
import ting.dto.Response;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Response<Void>> handleExceptions(Throwable e, WebRequest webRequest) {
        return new ResponseEntity<>(new Response<>(new ResponseError("Internal server error")), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
