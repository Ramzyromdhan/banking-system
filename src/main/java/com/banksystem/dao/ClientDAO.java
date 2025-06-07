// src/main/java/com/banksystem/dao/ClientDAO.java
package com.banksystem.dao;

import com.banksystem.model.Client;
import com.banksystem.util.DatabaseConnection; // Pour obtenir la connexion à la DB

import java.sql.*;
import java.util.Optional; // Pour gérer l'absence de résultat

public class ClientDAO {

    /**
     * Enregistre un nouveau client dans la base de données.
     *
     * @param client L'objet Client à enregistrer.
     * @return true si le client est ajouté avec succès, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean addClient(Client client) throws SQLException {
        String sql = "INSERT INTO clients (id, nom, prenom, email, telephone, adresse, mot_de_passe_hash) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, client.getId());
            statement.setString(2, client.getNom());
            statement.setString(3, client.getPrenom());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getTelephone());
            statement.setString(6, client.getAdresse());
            statement.setString(7, client.getMotDePasseHash()); // Le hachage est déjà dans l'objet Client

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Récupère un client par son email.
     * Utile pour l'authentification.
     *
     * @param email L'email du client.
     * @return Un Optional contenant l'objet Client si trouvé, ou un Optional.empty() sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Optional<Client> getClientByEmail(String email) throws SQLException {
        String sql = "SELECT id, nom, prenom, email, telephone, adresse, mot_de_passe_hash FROM clients WHERE email = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String tele = resultSet.getString("telephone");
                String adresse = resultSet.getString("adresse");
                String motDePasseHash = resultSet.getString("mot_de_passe_hash");

                // Note: La liste de comptes du client n'est pas chargée ici,
                // elle sera chargée par un CompteDAO si nécessaire.
                Client client = new Client(id, nom, prenom, email, tele, adresse, motDePasseHash);
                return Optional.of(client);
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return Optional.empty(); // Aucun client trouvé
    }

    /**
     * Récupère un client par son ID.
     *
     * @param idClient L'ID du client.
     * @return Un Optional contenant l'objet Client si trouvé, ou un Optional.empty() sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Optional<Client> getClientById(String idClient) throws SQLException {
        String sql = "SELECT id, nom, prenom, email, telephone, adresse, mot_de_passe_hash FROM clients WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, idClient);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                String tele = resultSet.getString("telephone");
                String adresse = resultSet.getString("adresse");
                String motDePasseHash = resultSet.getString("mot_de_passe_hash");

                Client client = new Client(id, nom, prenom, email, tele, adresse, motDePasseHash);
                return Optional.of(client);
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return Optional.empty(); // Aucun client trouvé
    }

    // Ajoutez d'autres méthodes CRUD (Create, Read, Update, Delete) si nécessaire :
    // public boolean updateClient(Client client) ...
    // public boolean deleteClient(String idClient) ...
    // public List<Client> getAllClients() ...
}