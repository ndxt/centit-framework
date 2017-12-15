package com.centit.framework.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = { IOException.class , RuntimeException.class })
    public String exception(Exception exception, WebRequest request) {
        logger.info("Catch an exception", exception);
        return  "redirect:/system/exception/error/500";
    }

    @ExceptionHandler(value = { NoHandlerFoundException.class })
    public String noMapping(Exception exception, WebRequest request) {
        logger.info("No mapping exception", exception);
        return "redirect:/system/exception/error/404";
    }
}
