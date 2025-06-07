// src/main/java/com/banksystem/model/Transaction.java
package com.banksystem.model;

import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String type;       // "DEBIT", "CREDIT", "TRANSFERT" - Utilisez une énumération réelle en Java
    private double montant;
    private LocalDateTime date;
    private String description;
    private Compte compteSource; // Le compte d'où l'argent provient
    private Compte compteDestination; // Le compte où l'argent va (peut être null pour DEBIT/CREDIT)

    // Constructeur
    public Transaction(String id, String type, double montant, LocalDateTime date, String description, Compte compteSource, Compte compteDestination) {
        this.id = id;
        this.type = type;
        this.montant = montant;
        this.date = date;
        this.description = description;
        this.compteSource = compteSource;
        this.compteDestination = compteDestination;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getMontant() {
        return montant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Compte getCompteSource() {
        return compteSource;
    }

    public Compte getCompteDestination() {
        return compteDestination;
    }

    // --- Setters (si nécessaire, mais les transactions sont généralement immuables) ---
    // Les setters ne sont généralement pas utilisés pour les transactions une fois créées,
    // car une transaction représente un événement figé dans le temps.

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", montant=" + montant +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", compteSource=" + (compteSource != null ? compteSource.getNumero() : "N/A") +
                ", compteDestination=" + (compteDestination != null ? compteDestination.getNumero() : "N/A") +
                '}';
    }
}
