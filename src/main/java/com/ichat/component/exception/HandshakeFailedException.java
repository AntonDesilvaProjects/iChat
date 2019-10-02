package com.ichat.component.exception;

public class HandshakeFailedException extends RuntimeException{
    public HandshakeFailedException(String message) {
        super(message);
    }
}
