// src/main/java/com/banksystem/model/CompteCourant.java
package com.banksystem.model;

import com.banksystem.exception.SoldeInsuffisantException;

public class CompteCourant extends Compte {
    private double decouvertAutorise;

    public CompteCourant(String numero, double solde, Client client, double decouvertAutorise) {
        super(numero, solde, client);
        this.decouvertAutorise = decouvertAutorise;
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(double decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }

    // --- Implémentation des méthodes abstraites de Compte ---
    @Override
    public void deposer(double montant) {
        if (montant > 0) {
            this.solde += montant;
            System.out.println("Dépôt de " + montant + " EUR effectué sur le compte courant " + numero + ". Nouveau solde : " + solde + " EUR");
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

        // Vérifie si le retrait est possible en tenant compte du découvert autorisé
        if (this.solde - montant >= -decouvertAutorise) {
            this.solde -= montant;
            System.out.println("Retrait de " + montant + " EUR effectué sur le compte courant " + numero + ". Nouveau solde : " + solde + " EUR");
        } else {
            throw new SoldeInsuffisantException("Fonds insuffisants pour le retrait sur le compte " + numero + ". Découvert autorisé : " + decouvertAutorise);
        }
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "numero='" + numero + '\'' +
                ", solde=" + solde +
                ", decouvertAutorise=" + decouvertAutorise +
                ", client=" + (client != null ? client.getNomComplet() : "N/A") +
                '}';
    }
}