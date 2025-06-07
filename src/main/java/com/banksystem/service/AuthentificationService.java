// src/main/java/com/banksystem/service/AuthentificationService.java (MODIFIÉ)
package com.banksystem.service;

import com.banksystem.model.Client;
import com.banksystem.model.Admin;
import com.banksystem.dao.ClientDAO; // Importer le DAO
import com.banksystem.dao.AdminDAO;   // Importer le DAO
import com.banksystem.exception.AuthentificationException;

import java.sql.SQLException;
import java.util.Optional;

public class AuthentificationService {

    private GestionMotDePasse gestionMotDePasse;
    private ClientDAO clientDAO; // Dépendance vers ClientDAO
    private AdminDAO adminDAO;   // Dépendance vers AdminDAO

    public AuthentificationService() {
        this.gestionMotDePasse = new GestionMotDePasse();
        this.clientDAO = new ClientDAO(); // Initialiser ClientDAO
        this.adminDAO = new AdminDAO();   // Initialiser AdminDAO
    }

    /**
     * Tente d'authentifier un client en le cherchant dans la base de données.
     *
     * @param email L'email du client.
     * @param motDePasse Le mot de passe en clair fourni par le client.
     * @return L'objet Client si l'authentification réussit.
     * @throws AuthentificationException Si l'authentification échoue (mauvais email/mot de passe, ou erreur DB).
     */
    public Client authentifierClient(String email, String motDePasse) throws AuthentificationException {
        try {
            // 1. Chercher le client par email dans la base de données
            Optional<Client> clientOptional = clientDAO.getClientByEmail(email);

            // 2. Vérifier si un client a été trouvé
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                // 3. Vérifier le mot de passe fourni avec le hachage stocké
                if (gestionMotDePasse.verifier(motDePasse, client.getMotDePasseHash())) {
                    return client; // Authentification réussie
                }
            }
            // Si pas de client trouvé ou mot de passe incorrect
            throw new AuthentificationException("Email ou mot de passe client incorrect.");

        } catch (SQLException e) {
            // Gérer les erreurs de base de données
            throw new AuthentificationException("Erreur de connexion/base de données lors de l'authentification du client: " + e.getMessage());
        }
    }

    /**
     * Tente d'authentifier un administrateur en le cherchant dans la base de données.
     *
     * @param login Le login de l'administrateur.
     * @param motDePasse Le mot de passe en clair fourni par l'admin.
     * @return L'objet Admin si l'authentification réussit.
     * @throws AuthentificationException Si l'authentification échoue (mauvais login/mot de passe, ou erreur DB).
     */
    public Admin authentifierAdmin(String login, String motDePasse) throws AuthentificationException {
        try {
            // 1. Chercher l'administrateur par login dans la base de données
            Optional<Admin> adminOptional = adminDAO.getAdminByLogin(login);

            // 2. Vérifier si un administrateur a été trouvé
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                // 3. Vérifier le mot de passe fourni avec le hachage stocké
                if (gestionMotDePasse.verifier(motDePasse, admin.getMotDePasseHash())) {
                    return admin; // Authentification réussie
                }
            }
            // Si pas d'admin trouvé ou mot de passe incorrect
            throw new AuthentificationException("Login ou mot de passe administrateur incorrect.");

        } catch (SQLException e) {
            // Gérer les erreurs de base de données
            throw new AuthentificationException("Erreur de connexion/base de données lors de l'authentification de l'administrateur: " + e.getMessage());
        }
    }
}