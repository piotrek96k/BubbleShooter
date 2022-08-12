package com.piotrek.exception;

public class IllegalValueException extends RuntimeException {

    private static final long serialVersionUID = -4947697994166503072L;

    public IllegalValueException(String message) {
        super(message);
    }

}
