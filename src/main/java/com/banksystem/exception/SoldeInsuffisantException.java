// src/main/java/com/banksystem/exception/SoldeInsuffisantException.java
package com.banksystem.exception;

public class SoldeInsuffisantException extends RuntimeException {
    public SoldeInsuffisantException(String message) {
        super(message);
    }
}