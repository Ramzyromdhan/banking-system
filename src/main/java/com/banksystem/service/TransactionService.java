// src/main/java/com/banksystem/service/TransactionService.java
package com.banksystem.service;

import com.banksystem.dao.CompteDAO;
import com.banksystem.dao.TransactionDAO;
import com.banksystem.exception.CompteNonTrouveException;
import com.banksystem.exception.SoldeInsuffisantException;
import com.banksystem.model.Compte;
import com.banksystem.model.Transaction;
import com.banksystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {

    private CompteDAO compteDAO;
    private TransactionDAO transactionDAO;

    public TransactionService() {
        this.compteDAO = new CompteDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Effectue un virement d'un compte source vers un compte destination.
     * Cette méthode gère la logique métier de retrait du compte source,
     * de dépôt sur le compte destination et de l'enregistrement de la transaction.
     *
     * @param numeroCompteSource Le numéro du compte d'où l'argent sera retiré.
     * @param numeroCompteDestination Le numéro du compte où l'argent sera déposé.
     * @param montant Le montant à transférer.
     * @param description Une description facultative de la transaction.
     * @throws CompteNonTrouveException Si l'un des comptes n'existe pas.
     * @throws SoldeInsuffisantException Si le compte source n'a pas les fonds suffisants.
     * @throws IllegalArgumentException Si le montant est négatif ou si les comptes sont identiques.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public void effectuerVirement(String numeroCompteSource, String numeroCompteDestination, double montant, String description)
            throws CompteNonTrouveException, SoldeInsuffisantException, IllegalArgumentException, SQLException {

        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du virement doit être positif.");
        }
        if (numeroCompteSource.equals(numeroCompteDestination)) {
            throw new IllegalArgumentException("Le compte source et le compte destination ne peuvent pas être identiques pour un virement.");
        }

        // 1. Récupérer les comptes depuis la base de données
        Optional<Compte> sourceOptional = compteDAO.getCompteByNumero(numeroCompteSource);
        Optional<Compte> destinationOptional = compteDAO.getCompteByNumero(numeroCompteDestination);

        if (!sourceOptional.isPresent()) {
            throw new CompteNonTrouveException("Compte source non trouvé : " + numeroCompteSource);
        }
        if (!destinationOptional.isPresent()) {
            throw new CompteNonTrouveException("Compte destination non trouvé : " + numeroCompteDestination);
        }

        Compte compteSource = sourceOptional.get();
        Compte compteDestination = destinationOptional.get();

        Connection connection = null; // Gérer la connexion pour la transaction SQL (ACID)
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Démarrer une transaction (très important pour les virements !)

            // 2. Tenter de retirer le montant du compte source
            // La méthode retirer du modèle gère la logique de solde insuffisant et découvert
            // et lance SoldeInsuffisantException si nécessaire.
            compteSource.retirer(montant); // Modifie l'objet Compte en mémoire

            // 3. Tenter de déposer le montant sur le compte destination
            compteDestination.deposer(montant); // Modifie l'objet Compte en mémoire

            // 4. Mettre à jour les soldes dans la base de données
            if (!compteDAO.updateCompte(compteSource)) {
                throw new SQLException("Échec de la mise à jour du compte source lors du virement.");
            }
            if (!compteDAO.updateCompte(compteDestination)) {
                throw new SQLException("Échec de la mise à jour du compte destination lors du virement.");
            }

            // 5. Enregistrer la transaction
            Transaction transaction = new Transaction(
                    "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    "TRANSFERT", // Type de transaction
                    montant,
                    LocalDateTime.now(),
                    description,
                    compteSource,
                    compteDestination
            );
            if (!transactionDAO.addTransaction(transaction)) {
                throw new SQLException("Impossible d'enregistrer la transaction de virement.");
            }

            connection.commit(); // Confirmer la transaction SQL
            System.out.println("Virement de " + montant + " EUR de " + numeroCompteSource + " vers " + numeroCompteDestination + " effectué avec succès.");

        } catch (SQLException | SoldeInsuffisantException | CompteNonTrouveException | IllegalArgumentException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Annuler la transaction SQL en cas d'erreur
                    System.err.println("Virement annulé en raison d'une erreur.");
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur lors de l'annulation de la transaction: " + rollbackEx.getMessage());
                }
            }
            // Rendre l'exception originale pour que l'appelant puisse la gérer
            if (e instanceof SQLException) {
                throw (SQLException)e;
            } else if (e instanceof SoldeInsuffisantException) {
                throw (SoldeInsuffisantException) e;
            } else if (e instanceof CompteNonTrouveException) {
                throw (CompteNonTrouveException) e;
            } else { // IllegalArgumentException
                throw (IllegalArgumentException) e;
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Rétablir le mode auto-commit
                } catch (SQLException e) {
                    System.err.println("Erreur lors du rétablissement de l'auto-commit : " + e.getMessage());
                }
                DatabaseConnection.closeConnection(connection); // Fermer la connexion
            }
        }
    }

    /**
     * Récupère l'historique des transactions pour un compte donné.
     *
     * @param numeroCompte Le numéro du compte.
     * @return La liste des transactions pour ce compte.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public List<Transaction> getHistoriqueTransactions(String numeroCompte) throws SQLException {
        return transactionDAO.getTransactionsByCompteNumero(numeroCompte);
    }
}
