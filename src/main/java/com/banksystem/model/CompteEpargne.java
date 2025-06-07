// src/main/java/com/banksystem/model/CompteEpargne.java
package com.banksystem.model;

import com.banksystem.exception.SoldeInsuffisantException;

public class CompteEpargne extends Compte {
    private double tauxInteret;

    public CompteEpargne(String numero, double solde, Client client, double tauxInteret) {
        super(numero, solde, client);
        this.tauxInteret = tauxInteret;
    }

    // --- Getter ---
    public double getTauxInteret() {
        return tauxInteret;
    }

    // --- Setter ---
    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }

    // --- Méthodes spécifiques au Compte Epargne ---
    public double calculerInterets() {
        return this.solde * (tauxInteret / 100.0);
    }

    // --- Implémentation des méthodes abstraites de Compte ---
    @Override
    public void deposer(double montant) {
        if (montant > 0) {
            this.solde += montant;
            System.out.println("Dépôt de " + montant + " EUR effectué sur le compte épargne " + numero + ". Nouveau solde : " + solde + " EUR");
        } else {
            System.out.println("Le montant du dépôt doit être positif.");
        }
    }

    @Override
    public void retirer(double montant) {
        if (montant <= 0) {
            System.out.println("Le montant du retrait doit être positif.");
            return;
        }

        if (this.solde >= montant) {
            this.solde -= montant;
            System.out.println("Retrait de " + montant + " EUR effectué sur le compte épargne " + numero + ". Nouveau solde : " + solde + " EUR");
        } else {
            throw new SoldeInsuffisantException("Fonds insuffisants pour le retrait sur le compte épargne " + numero + ". Pas de découvert autorisé.");
        }
    }

    @Override
    public String toString() {
        return "CompteEpargne{" +
                "numero='" + numero + '\'' +
                ", solde=" + solde +
                ", tauxInteret=" + tauxInteret +
                ", client=" + (client != null ? client.getNomComplet() : "N/A") +
                '}';
    }
}