package org.myplaylist.myplaylist.exception;


public class CustomValidationException extends RuntimeException {
    private String filed;

    public CustomValidationException(String message, String filed) {
        super(message);
        this.filed = filed;
    }
    public String getFiled() {
        return filed;
    }
}


