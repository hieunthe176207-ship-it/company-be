package com.swp.company.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiException extends Exception {
    private int status;
    private String message;

    public ApiException(int status, String message) {
        super(message);
        this.message = message;
        this.status = status;

    }
}