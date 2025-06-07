// src/main/java/com/banksystem/app/BankApplication.java
package com.banksystem.app;

import com.banksystem.model.Client;
import com.banksystem.model.Admin;
import com.banksystem.service.AuthentificationService;
import com.banksystem.service.GestionMotDePasse;
import com.banksystem.dao.ClientDAO; // Pour ajouter un client de test
import com.banksystem.dao.AdminDAO;   // Pour ajouter un admin de test
import com.banksystem.exception.AuthentificationException;

import java.sql.SQLException;
import java.util.UUID; // Pour générer des IDs uniques

public class BankApplication {

    public static void main(String[] args) {
        AuthentificationService authService = new AuthentificationService();
        GestionMotDePasse passwordManager = new GestionMotDePasse();
        ClientDAO clientDAO = new ClientDAO();
        AdminDAO adminDAO = new AdminDAO();


        try {
            // Ajout d'un client de test
            String clientPassHash = passwordManager.hasher("clientpass");
            Client testClient = new Client(
                    "C-" + UUID.randomUUID().toString().substring(0, 8), // ID unique
                    "Test", "Client", "client@example.com", "0123456789", "Adresse Test", clientPassHash
            );
            if (clientDAO.addClient(testClient)) {
                System.out.println("Client de test ajouté : " + testClient.getEmail());
            } else {
                System.out.println("Client de test déjà existant ou erreur d'ajout.");
            }

            // Ajout d'un admin de test
            String adminPassHash = passwordManager.hasher("adminpass");
            Admin testAdmin = new Admin(
                    "A-" + UUID.randomUUID().toString().substring(0, 8), // ID unique
                    "superadmin", adminPassHash
            );
            if (adminDAO.addAdmin(testAdmin)) {
                System.out.println("Admin de test ajouté : " + testAdmin.getLogin());
            } else {
                System.out.println("Admin de test déjà existant ou erreur d'ajout.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des utilisateurs de test à la DB : " + e.getMessage());
            // Si c'est une erreur de clé dupliquée (unique email/login), c'est normal pour des insertions répétées
        }


        // --- TESTS D'AUTHENTIFICATION ---
        System.out.println("\n--- Tests d'authentification ---");

        // Test de connexion client réussie
        try {
            Client authenticatedClient = authService.authentifierClient("client@example.com", "clientpass");
            System.out.println("Authentification Client réussie : " + authenticatedClient.getNomComplet());
        } catch (AuthentificationException e) {
            System.out.println("Authentification Client échouée : " + e.getMessage());
        }

        // Test de connexion client échouée (mauvais mot de passe)
        try {
            authService.authentifierClient("client@example.com", "mauvaispass");
        } catch (AuthentificationException e) {
            System.out.println("Authentification Client échouée (attendu) : " + e.getMessage());
        }

        // Test de connexion client échouée (email inconnu)
        try {
            authService.authentifierClient("inconnu@example.com", "password");
        } catch (AuthentificationException e) {
            System.out.println("Authentification Client échouée (attendu) : " + e.getMessage());
        }

        // Test de connexion admin réussie
        try {
            Admin authenticatedAdmin = authService.authentifierAdmin("superadmin", "adminpass");
            System.out.println("Authentification Admin réussie : " + authenticatedAdmin.getLogin());
        } catch (AuthentificationException e) {
            System.out.println("Authentification Admin échouée : " + e.getMessage());
        }

        // Test de connexion admin échouée (mauvais mot de passe)
        try {
            authService.authentifierAdmin("superadmin", "badpass");
        } catch (AuthentificationException e) {
            System.out.println("Authentification Admin échouée (attendu) : " + e.getMessage());
        }
    }
}