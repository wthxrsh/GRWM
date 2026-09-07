package com.wthxrsh.grwm.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {

    public ApiError(int status, String error, String message) {
        this(status, error, message, LocalDateTime.now(), null);
    }

    public ApiError(int status, String error, String message, Map<String, String> fieldErrors) {
        this(status, error, message, LocalDateTime.now(), fieldErrors);
    }
}