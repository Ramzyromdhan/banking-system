// src/main/java/com/banksystem/dao/CompteDAO.java
package com.banksystem.dao;

import com.banksystem.model.Client;
import com.banksystem.model.Compte;
import com.banksystem.model.CompteCourant;
import com.banksystem.model.CompteEpargne;
import com.banksystem.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteDAO {

    private ClientDAO clientDAO = new ClientDAO(); // Pour récupérer l'objet Client associé

    /**
     * Enregistre un nouveau compte (courant ou épargne) dans la base de données.
     *
     * @param compte L'objet Compte à enregistrer.
     * @return true si le compte est ajouté avec succès, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean addCompte(Compte compte) throws SQLException {
        String sql = "INSERT INTO comptes (numero, solde, date_ouverture, client_id, type_compte, decouvert_autorise, taux_interet) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, compte.getNumero());
            statement.setDouble(2, compte.getSolde());
            statement.setDate(3, Date.valueOf(compte.getDateOuverture()));
            statement.setString(4, compte.getClient().getId()); // Récupère l'ID du client

            if (compte instanceof CompteCourant) {
                CompteCourant cc = (CompteCourant) compte;
                statement.setString(5, "COURANT");
                statement.setDouble(6, cc.getDecouvertAutorise());
                statement.setNull(7, Types.DOUBLE); // Pas de taux d'intérêt pour un compte courant
            } else if (compte instanceof CompteEpargne) {
                CompteEpargne ce = (CompteEpargne) compte;
                statement.setString(5, "EPARGNE");
                statement.setNull(6, Types.DOUBLE); // Pas de découvert pour un compte épargne
                statement.setDouble(7, ce.getTauxInteret());
            } else {
                throw new SQLException("Type de compte non supporté.");
            }

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
     * Récupère un compte par son numéro.
     *
     * @param numeroCompte Le numéro du compte.
     * @return Un Optional contenant l'objet Compte si trouvé, ou un Optional.empty() sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Optional<Compte> getCompteByNumero(String numeroCompte) throws SQLException {
        String sql = "SELECT numero, solde, date_ouverture, client_id, type_compte, decouvert_autorise, taux_interet FROM comptes WHERE numero = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, numeroCompte);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String numero = resultSet.getString("numero");
                double solde = resultSet.getDouble("solde");
                LocalDate dateOuverture = resultSet.getDate("date_ouverture").toLocalDate();
                String clientId = resultSet.getString("client_id");
                String typeCompte = resultSet.getString("type_compte");
                double decouvertAutorise = resultSet.getDouble("decouvert_autorise");
                double tauxInteret = resultSet.getDouble("taux_interet");

                // Récupérer l'objet Client associé
                Optional<Client> clientOptional = clientDAO.getClientById(clientId);
                if (!clientOptional.isPresent()) {
                    throw new SQLException("Client associé au compte non trouvé : " + clientId);
                }
                Client client = clientOptional.get();

                Compte compte;
                if ("COURANT".equals(typeCompte)) {
                    compte = new CompteCourant(numero, solde, client, decouvertAutorise);
                } else if ("EPARGNE".equals(typeCompte)) {
                    compte = new CompteEpargne(numero, solde, client, tauxInteret);
                } else {
                    throw new SQLException("Type de compte inconnu dans la base de données : " + typeCompte);
                }
                return Optional.of(compte);
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
        return Optional.empty();
    }

    /**
     * Met à jour le solde et les paramètres spécifiques d'un compte.
     *
     * @param compte L'objet Compte avec les informations à mettre à jour.
     * @return true si la mise à jour réussit, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean updateCompte(Compte compte) throws SQLException {
        String sql = "UPDATE comptes SET solde = ?, decouvert_autorise = ?, taux_interet = ? WHERE numero = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, compte.getSolde());

            if (compte instanceof CompteCourant) {
                CompteCourant cc = (CompteCourant) compte;
                statement.setDouble(2, cc.getDecouvertAutorise());
                statement.setNull(3, Types.DOUBLE); // Pas de taux d'intérêt
            } else if (compte instanceof CompteEpargne) {
                CompteEpargne ce = (CompteEpargne) compte;
                statement.setNull(2, Types.DOUBLE); // Pas de découvert
                statement.setDouble(3, ce.getTauxInteret());
            } else {
                throw new SQLException("Type de compte non supporté pour la mise à jour.");
            }
            statement.setString(4, compte.getNumero());

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
     * Supprime un compte de la base de données par son numéro.
     *
     * @param numeroCompte Le numéro du compte à supprimer.
     * @return true si le compte est supprimé avec succès, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean deleteCompte(String numeroCompte) throws SQLException {
        String sql = "DELETE FROM comptes WHERE numero = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, numeroCompte);

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
     * Récupère tous les comptes associés à un client donné.
     *
     * @param clientId L'ID du client.
     * @return Une liste de tous les comptes du client.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Compte> getComptesByClientId(String clientId) throws SQLException {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT numero, solde, date_ouverture, client_id, type_compte, decouvert_autorise, taux_interet FROM comptes WHERE client_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, clientId);
            resultSet = statement.executeQuery();

            // Récupérer le client une seule fois pour tous les comptes
            Optional<Client> clientOptional = clientDAO.getClientById(clientId);
            if (!clientOptional.isPresent()) {
                throw new SQLException("Client associé aux comptes non trouvé : " + clientId);
            }
            Client client = clientOptional.get();

            while (resultSet.next()) {
                String numero = resultSet.getString("numero");
                double solde = resultSet.getDouble("solde");
                LocalDate dateOuverture = resultSet.getDate("date_ouverture").toLocalDate();
                String typeCompte = resultSet.getString("type_compte");
                double decouvertAutorise = resultSet.getDouble("decouvert_autorise");
                double tauxInteret = resultSet.getDouble("taux_interet");

                Compte compte;
                if ("COURANT".equals(typeCompte)) {
                    compte = new CompteCourant(numero, solde, client, decouvertAutorise);
                } else if ("EPARGNE".equals(typeCompte)) {
                    compte = new CompteEpargne(numero, solde, client, tauxInteret);
                } else {
                    System.err.println("Type de compte inconnu trouvé dans la base de données : " + typeCompte);
                    continue; // Passer ce compte et continuer
                }
                comptes.add(compte);
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
        return comptes;
    }
}