// src/main/java/com/banksystem/model/Client.java (MODIFIÉ)
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
    private List<Compte> comptes; // Note: Cette liste n'est pas persistée directement,
    // elle est chargée ou manipulée en mémoire.

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

    // --- Setters (si nécessaire, attention à la sécurité des mots de passe) ---
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

    // Le mot de passe ne doit être changé que via un service dédié, pas directement
    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    // --- Méthodes spécifiques au Client ---
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    // Cette méthode ajoute un compte à la liste en mémoire du client.
    // L'ajout réel en DB est géré par CompteService/CompteDAO.
    public void ajouterCompte(Compte c) {
        if (c != null && !this.comptes.contains(c)) {
            this.comptes.add(c);
        }
    }

    // Cette méthode supprime un compte de la liste en mémoire du client.
    // La suppression réelle en DB est gérée par CompteService/CompteDAO.
    public void supprimerCompte(String numeroCompte) {
        this.comptes.removeIf(compte -> compte.getNumero().equals(numeroCompte));
        System.out.println("Compte " + numeroCompte + " désassocié du client " + this.getNomComplet() + " en mémoire.");
    }

    // La logique du virement sera gérée par TransactionService.
    // Le client ne fait qu'initier l'action.
    public void effectuerVirement(Compte compteSource, Compte compteDestination, double montant) {
        // Cette méthode est maintenant un simple "appel" pour le client.
        // L'implémentation réelle de la logique de virement est dans TransactionService.
        // Une instance de TransactionService devrait être injectée ou accessible ici.
        System.out.println("Le client " + getNomComplet() + " initie un virement de " + montant + " EUR de " + compteSource.getNumero() + " vers " + compteDestination.getNumero() + ".");
        // Le code réel pour appeler le service sera dans l'application ou un contrôleur.
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
