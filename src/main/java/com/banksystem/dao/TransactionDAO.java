// src/main/java/com/banksystem/dao/TransactionDAO.java
package com.banksystem.dao;

import com.banksystem.model.Compte;
import com.banksystem.model.Transaction;
import com.banksystem.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO {

    // Nous aurons besoin de CompteDAO pour reconstituer les objets Compte
    private CompteDAO compteDAO = new CompteDAO();

    /**
     * Enregistre une nouvelle transaction dans la base de données.
     *
     * @param transaction L'objet Transaction à enregistrer.
     * @return true si la transaction est ajoutée avec succès, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (id, type_transaction, montant, date_transaction, description, compte_source_numero, compte_destination_numero) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, transaction.getId());
            statement.setString(2, transaction.getType());
            statement.setDouble(3, transaction.getMontant());
            statement.setTimestamp(4, Timestamp.valueOf(transaction.getDate()));
            statement.setString(5, transaction.getDescription());
            statement.setString(6, transaction.getCompteSource().getNumero());
            // Si compteDestination est null (ex: dépôt/retrait), on insère null en DB
            if (transaction.getCompteDestination() != null) {
                statement.setString(7, transaction.getCompteDestination().getNumero());
            } else {
                statement.setNull(7, Types.VARCHAR);
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
     * Récupère toutes les transactions associées à un compte donné (en tant que source ou destination).
     *
     * @param numeroCompte Le numéro du compte.
     * @return Une liste de transactions pour ce compte.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Transaction> getTransactionsByCompteNumero(String numeroCompte) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, type_transaction, montant, date_transaction, description, compte_source_numero, compte_destination_numero FROM transactions WHERE compte_source_numero = ? OR compte_destination_numero = ? ORDER BY date_transaction DESC";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, numeroCompte);
            statement.setString(2, numeroCompte); // Pour les transactions où c'est la destination
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String type = resultSet.getString("type_transaction");
                double montant = resultSet.getDouble("montant");
                LocalDateTime date = resultSet.getTimestamp("date_transaction").toLocalDateTime();
                String description = resultSet.getString("description");
                String compteSourceNumero = resultSet.getString("compte_source_numero");
                String compteDestinationNumero = resultSet.getString("compte_destination_numero");

                // Reconstituer les objets Compte
                Optional<Compte> sourceCompte = compteDAO.getCompteByNumero(compteSourceNumero);
                if (!sourceCompte.isPresent()) {
                    System.err.println("Compte source non trouvé pour la transaction " + id + ": " + compteSourceNumero);
                    continue; // Passer cette transaction si le compte source n'est pas trouvé
                }

                Compte destinationCompte = null;
                if (compteDestinationNumero != null) {
                    Optional<Compte> destCompte = compteDAO.getCompteByNumero(compteDestinationNumero);
                    if (destCompte.isPresent()) {
                        destinationCompte = destCompte.get();
                    } else {
                        System.err.println("Compte destination non trouvé pour la transaction " + id + ": " + compteDestinationNumero);
                        // Vous pouvez choisir de jeter une exception ou de continuer avec un compte destination null
                    }
                }

                Transaction transaction = new Transaction(id, type, montant, date, description, sourceCompte.get(), destinationCompte);
                transactions.add(transaction);
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
        return transactions;
    }
}
