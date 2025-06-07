// src/main/java/com/banksystem/exception/CompteNonTrouveException.java
package com.banksystem.exception;

public class CompteNonTrouveException extends RuntimeException {
    public CompteNonTrouveException(String message) {
        super(message);
    }

    public CompteNonTrouveException(String message, Throwable cause) {
        super(message, cause);
    }
}
