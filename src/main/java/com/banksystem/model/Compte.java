// src/main/java/com/banksystem/model/Compte.java
package com.banksystem.model;

import java.time.LocalDate;

public abstract class Compte {
    protected String numero;
    protected double solde;
    protected LocalDate dateOuverture;
    protected Client client; // Le client propriétaire du compte

    // Constructeur
    public Compte(String numero, double solde, Client client) {
        this.numero = numero;
        this.solde = solde;
        this.dateOuverture = LocalDate.now(); // Date d'ouverture par défaut à aujourd'hui
        this.client = client;
    }

    // --- Getters ---
    public String getNumero() {
        return numero;
    }

    public double getSolde() {
        return solde;
    }

    public LocalDate getDateOuverture() {
        return dateOuverture;
    }

    public Client getClient() {
        return client;
    }

    // --- Setters ---

    public void setSolde(double solde) {
        this.solde = solde;
    }


    public abstract void deposer(double montant);
    public abstract void retirer(double montant);


    public void afficherSolde() {
        System.out.println("Le solde du compte " + numero + " est : " + solde + " EUR");
    }

    @Override
    public String toString() {
        return "Compte{" +
                "numero='" + numero + '\'' +
                ", solde=" + solde +
                ", dateOuverture=" + dateOuverture +
                ", client=" + (client != null ? client.getNomComplet() : "N/A") +
                '}';
    }
}