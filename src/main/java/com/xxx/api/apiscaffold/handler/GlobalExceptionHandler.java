package com.xxx.api.apiscaffold.handler;

import com.xxx.api.apiscaffold.util.ResponseEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity handleException(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception) {
        String information = exception.getMessage();
        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException t = (MethodArgumentNotValidException) exception;
            information = t.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
        } else if (exception instanceof BindException) {
            BindException t = (BindException) exception;
            information = t.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException t = (ConstraintViolationException) exception;
            information = t.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
        } else if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException t = (MissingServletRequestParameterException) exception;
            information = t.getParameterName() + " 不能为空";
        } else if (exception instanceof MissingPathVariableException) {
            MissingPathVariableException t = (MissingPathVariableException) exception;
            information = t.getVariableName() + " 不能为空";
        }

        LOGGER.error("访问 {} 返回异常: {}", request.getRequestURI(), information);
        LOGGER.error(exception.getMessage(), exception);

        return ResponseEntityBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), information);
    }

}
