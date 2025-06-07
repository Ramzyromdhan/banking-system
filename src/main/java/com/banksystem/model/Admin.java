// src/main/java/com/banksystem/model/Admin.java
package com.banksystem.model;

public class Admin {
    private String id;
    private String login;
    private String motDePasseHash;

    public Admin(String id, String login, String motDePasseHash) {
        this.id = id;
        this.login = login;
        this.motDePasseHash = motDePasseHash;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }


    public void setLogin(String login) {
        this.login = login;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}