// src/main/java/com/banksystem/service/CompteService.java
package com.banksystem.service;

import com.banksystem.dao.CompteDAO;
import com.banksystem.dao.TransactionDAO; // Pour enregistrer les transactions
import com.banksystem.exception.CompteNonTrouveException;
import com.banksystem.exception.SoldeInsuffisantException;
import com.banksystem.model.Client;
import com.banksystem.model.Compte;
import com.banksystem.model.CompteCourant;
import com.banksystem.model.CompteEpargne;
import com.banksystem.model.Transaction;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID; // Pour générer des IDs uniques

public class CompteService {

    private CompteDAO compteDAO;
    private TransactionDAO transactionDAO; // Pour enregistrer les transactions

    public CompteService() {
        this.compteDAO = new CompteDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public CompteDAO getCompteDAO() {
        return compteDAO;
    }

    /**
     * Crée un nouveau compte courant pour un client et le sauvegarde en base de données.
     *
     * @param client Le client propriétaire du compte.
     * @param soldeInitial Le solde initial du compte.
     * @param decouvertAutorise Le découvert autorisé pour ce compte.
     * @return L'objet CompteCourant créé.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public CompteCourant creerCompteCourant(Client client, double soldeInitial, double decouvertAutorise) throws SQLException {
        String numeroCompte = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Génère un numéro unique
        CompteCourant newCompte = new CompteCourant(numeroCompte, soldeInitial, client, decouvertAutorise);
        if (compteDAO.addCompte(newCompte)) {
            client.ajouterCompte(newCompte); // Associe le compte au client en mémoire
            System.out.println("Compte courant " + numeroCompte + " créé pour " + client.getNomComplet());
            return newCompte;
        } else {
            throw new SQLException("Impossible de créer le compte courant.");
        }
    }

    /**
     * Crée un nouveau compte épargne pour un client et le sauvegarde en base de données.
     *
     * @param client Le client propriétaire du compte.
     * @param soldeInitial Le solde initial du compte.
     * @param tauxInteret Le taux d'intérêt pour ce compte.
     * @return L'objet CompteEpargne créé.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public CompteEpargne creerCompteEpargne(Client client, double soldeInitial, double tauxInteret) throws SQLException {
        String numeroCompte = "CE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Génère un numéro unique
        CompteEpargne newCompte = new CompteEpargne(numeroCompte, soldeInitial, client, tauxInteret);
        if (compteDAO.addCompte(newCompte)) {
            client.ajouterCompte(newCompte); // Associe le compte au client en mémoire
            System.out.println("Compte épargne " + numeroCompte + " créé pour " + client.getNomComplet());
            return newCompte;
        } else {
            throw new SQLException("Impossible de créer le compte épargne.");
        }
    }

    /**
     * Effectue un dépôt sur un compte spécifié et enregistre la transaction.
     *
     * @param numeroCompte Le numéro du compte cible.
     * @param montant Le montant à déposer.
     * @throws CompteNonTrouveException Si le compte n'existe pas.
     * @throws IllegalArgumentException Si le montant est négatif.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public void deposer(String numeroCompte, double montant) throws CompteNonTrouveException, SQLException, IllegalArgumentException {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être positif.");
        }

        Optional<Compte> compteOptional = compteDAO.getCompteByNumero(numeroCompte);
        if (!compteOptional.isPresent()) {
            throw new CompteNonTrouveException("Compte non trouvé avec le numéro : " + numeroCompte);
        }

        Compte compte = compteOptional.get();
        compte.deposer(montant); // Appelle la méthode de dépôt du modèle Compte

        // Met à jour le solde dans la base de données
        if (!compteDAO.updateCompte(compte)) {
            throw new SQLException("Erreur lors de la mise à jour du solde du compte " + numeroCompte);
        }

        // Enregistrer la transaction
        Transaction transaction = new Transaction(
                "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "CREDIT", // Type de transaction
                montant,
                LocalDateTime.now(),
                "Dépôt sur le compte " + numeroCompte,
                compte, // Compte source est le même que la destination pour un dépôt sur soi-même
                null // Pas de compte destination externe pour un dépôt
        );
        if (!transactionDAO.addTransaction(transaction)) {
            System.err.println("Avertissement: Impossible d'enregistrer la transaction de dépôt pour le compte " + numeroCompte);
        }
    }

    /**
     * Effectue un retrait d'un compte spécifié et enregistre la transaction.
     *
     * @param numeroCompte Le numéro du compte source.
     * @param montant Le montant à retirer.
     * @throws CompteNonTrouveException Si le compte n'existe pas.
     * @throws SoldeInsuffisantException Si le solde est insuffisant (y compris découvert).
     * @throws IllegalArgumentException Si le montant est négatif.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public void retirer(String numeroCompte, double montant) throws CompteNonTrouveException, SoldeInsuffisantException, SQLException, IllegalArgumentException {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif.");
        }

        Optional<Compte> compteOptional = compteDAO.getCompteByNumero(numeroCompte);
        if (!compteOptional.isPresent()) {
            throw new CompteNonTrouveException("Compte non trouvé avec le numéro : " + numeroCompte);
        }

        Compte compte = compteOptional.get();
        compte.retirer(montant); // Appelle la méthode de retrait du modèle Compte (gère les exceptions de solde)

        // Met à jour le solde dans la base de données
        if (!compteDAO.updateCompte(compte)) {
            throw new SQLException("Erreur lors de la mise à jour du solde du compte " + numeroCompte);
        }

        // Enregistrer la transaction
        Transaction transaction = new Transaction(
                "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "DEBIT", // Type de transaction
                montant,
                LocalDateTime.now(),
                "Retrait du compte " + numeroCompte,
                compte, // Compte source
                null // Pas de compte destination externe pour un retrait
        );
        if (!transactionDAO.addTransaction(transaction)) {
            System.err.println("Avertissement: Impossible d'enregistrer la transaction de retrait pour le compte " + numeroCompte);
        }
    }

    /**
     * Récupère un compte par son numéro.
     *
     * @param numeroCompte Le numéro du compte.
     * @return L'objet Compte si trouvé.
     * @throws CompteNonTrouveException Si le compte n'existe pas.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public Compte getCompte(String numeroCompte) throws CompteNonTrouveException, SQLException {
        return compteDAO.getCompteByNumero(numeroCompte)
                .orElseThrow(() -> new CompteNonTrouveException("Compte non trouvé : " + numeroCompte));
    }

    /**
     * Supprime un compte de la base de données.
     *
     * @param numeroCompte Le numéro du compte à supprimer.
     * @return true si la suppression réussit, false sinon.
     * @throws SQLException En cas d'erreur de base de données.
     */
    public boolean supprimerCompte(String numeroCompte) throws SQLException {
        Optional<Compte> compteOptional = compteDAO.getCompteByNumero(numeroCompte);
        if (!compteOptional.isPresent()) {
            System.out.println("Compte " + numeroCompte + " non trouvé pour suppression.");
            return false;
        }
        Compte compte = compteOptional.get();

        // Si le solde n'est pas zéro, on peut refuser la suppression
        if (compte.getSolde() != 0) {
            System.err.println("Impossible de supprimer le compte " + numeroCompte + " : Solde non nul (" + compte.getSolde() + " EUR).");
            return false;
        }

        // Dissocier le compte du client en mémoire (si le client est chargé)
        // Ceci nécessiterait que le client soit mis à jour en base de données aussi.
        // Pour l'instant, on se concentre sur la suppression physique du compte de la DB.
        // L'implémentation complète nécessiterait une gestion des clients en mémoire ou un rechargement.

        return compteDAO.deleteCompte(numeroCompte);
    }
}
