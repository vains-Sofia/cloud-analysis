package com.basic.cloud.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * 全局异常处理
 *
 * @author vains
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Map<String, String> exceptionHandler(Exception e) {
        return Map.of("error", e.getMessage());
    }

}
