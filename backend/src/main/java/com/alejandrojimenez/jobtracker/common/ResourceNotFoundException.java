package com.alejandrojimenez.jobtracker.common;

/** Thrown when a requested record does not exist; answered with 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
