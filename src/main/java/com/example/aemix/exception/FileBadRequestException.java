package com.example.aemix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class FileBadRequestException extends RuntimeException {
    public FileBadRequestException(String message) {
        super(message);
    }
}

