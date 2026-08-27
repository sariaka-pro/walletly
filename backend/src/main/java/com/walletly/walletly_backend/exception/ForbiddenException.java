package com.walletly.walletly_backend.exception; 


public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}