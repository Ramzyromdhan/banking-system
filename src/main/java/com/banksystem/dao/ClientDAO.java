// src/main/java/com/banksystem/dao/ClientDAO.java
package com.banksystem.dao;

import com.banksystem.model.Client;
import com.banksystem.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            statement.setString(7, client.getMotDePasseHash());

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

                // Note: La liste de comptes du client n'est pas chargée ici.
                // Elle sera chargée par un CompteDAO/CompteService si nécessaire.
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

    /**
     * Met à jour les informations d'un client existant dans la base de données.
     *
     * @param client L'objet Client avec les informations à mettre à jour (ID doit exister).
     * @return true si la mise à jour réussit, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean updateClient(Client client) throws SQLException {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ?, mot_de_passe_hash = ? WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, client.getNom());
            statement.setString(2, client.getPrenom());
            statement.setString(3, client.getEmail());
            statement.setString(4, client.getTelephone());
            statement.setString(5, client.getAdresse());
            statement.setString(6, client.getMotDePasseHash());
            statement.setString(7, client.getId()); // Condition WHERE sur l'ID

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
     * Supprime un client de la base de données par son ID.
     * Attention : Assurez-vous que les comptes associés au client sont gérés (supprimés ou réaffectés)
     * avant de supprimer le client, ou configurez une contrainte ON DELETE CASCADE dans la DB.
     *
     * @param idClient L'ID du client à supprimer.
     * @return true si le client est supprimé avec succès, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean deleteClient(String idClient) throws SQLException {
        // Logique pour supprimer les comptes du client d'abord si nécessaire,
        // ou la contrainte FOREIGN KEY (ON DELETE CASCADE) gérera cela.
        // Pour l'exemple, nous nous appuyons sur la contrainte DB si configurée,
        // sinon, une erreur SQL se produira si des comptes existent.
        String sql = "DELETE FROM clients WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, idClient);

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
     * Récupère tous les clients de la base de données.
     *
     * @return Une liste de tous les objets Client.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, telephone, adresse, mot_de_passe_hash FROM clients";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                String tele = resultSet.getString("telephone");
                String adresse = resultSet.getString("adresse");
                String motDePasseHash = resultSet.getString("mot_de_passe_hash");

                Client client = new Client(id, nom, prenom, email, tele, adresse, motDePasseHash);
                clients.add(client);
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
        return clients;
    }
}