// src/main/java/com/banksystem/service/GestionMotDePasse.java
package com.banksystem.service;

import org.mindrot.jbcrypt.BCrypt;

public class GestionMotDePasse {

    public String hasher(String password) {

        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public boolean verifier(String password, String hashedPassword) {

        return BCrypt.checkpw(password, hashedPassword);
    }
}