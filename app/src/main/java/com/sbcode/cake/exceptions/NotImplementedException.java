package com.sbcode.cake.exceptions;

/**
 * This exception is thrown if an inherited abstract method is not implemented.
 */
public class NotImplementedException extends RuntimeException {

    public NotImplementedException(String message) {
        super(message);
    }
}
