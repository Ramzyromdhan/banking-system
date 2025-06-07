// src/main/java/com/banksystem/model/Client.java
package com.banksystem.model;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String motDePasseHash; // Stocke le hachage du mot de passe
    private List<Compte> comptes;

    // Constructeur
    public Client(String id, String nom, String prenom, String email, String telephone, String adresse, String motDePasseHash) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.motDePasseHash = motDePasseHash;
        this.comptes = new ArrayList<>(); // Initialise la liste de comptes vide
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public List<Compte> getComptes() {
        return comptes;
    }

    // --- Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    // --- Méthodes spécifiques au Client ---
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public void ajouterCompte(Compte c) {
        if (c != null && !this.comptes.contains(c)) {
            this.comptes.add(c);
        }
    }

    public void supprimerCompte(String numeroCompte) {
        this.comptes.removeIf(compte -> compte.getNumero().equals(numeroCompte));
    }

    // La logique du virement sera gérée par un service, mais le client peut "initier" l'action
    // Pour l'instant, on laisse la signature, l'implémentation sera dans TransactionService
    public void effectuerVirement(Compte compteSource, Compte compteDestination, double montant) {
        // Cette méthode sera déléguée à TransactionService
        // Le client n'a pas la logique directe de manipulation des soldes
        // C'est juste un point d'entrée pour l'action du client.
        System.out.println("Le client " + getNomComplet() + " initie un virement de " + montant + " de " + compteSource.getNumero() + " vers " + compteDestination.getNumero());
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}