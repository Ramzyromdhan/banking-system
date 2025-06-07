// src/main/java/com/banksystem/app/BankApplication.java (MODIFIÉ)
package com.banksystem.app;

import com.banksystem.model.Client;
import com.banksystem.model.Admin;
import com.banksystem.model.Compte;
import com.banksystem.model.CompteCourant;
import com.banksystem.model.CompteEpargne;
import com.banksystem.model.Transaction;
import com.banksystem.service.AuthentificationService;
import com.banksystem.service.GestionMotDePasse;
import com.banksystem.service.CompteService;     // Nouveau service
import com.banksystem.service.TransactionService; // Nouveau service
import com.banksystem.dao.ClientDAO;
import com.banksystem.dao.AdminDAO;
import com.banksystem.exception.AuthentificationException;
import com.banksystem.exception.CompteNonTrouveException;
import com.banksystem.exception.SoldeInsuffisantException;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID; // Pour générer des IDs uniques

public class BankApplication {

    public static void main(String[] args) {
        AuthentificationService authService = new AuthentificationService();
        GestionMotDePasse passwordManager = new GestionMotDePasse();
        ClientDAO clientDAO = new ClientDAO();
        AdminDAO adminDAO = new AdminDAO();
        CompteService compteService = new CompteService(); // Nouveau
        TransactionService transactionService = new TransactionService(); // Nouveau


        // --- ÉTAPE DE PRÉPARATION : Ajouter des utilisateurs et des comptes de test dans la DB ---
        System.out.println("--- Préparation des données de test ---");
        Client client1 = null;
        Compte compte1 = null;
        Compte compte2 = null;
        Client client2 = null; // Déclaré ici
        Compte compte3 = null; // Déclaré ici

        try {
            // Ajout/Vérification d'un client de test
            String client1Email = "client1@example.com";
            if (!clientDAO.getClientByEmail(client1Email).isPresent()) {
                String clientPassHash = passwordManager.hasher("pass123");
                client1 = new Client("C-" + UUID.randomUUID().toString().substring(0, 8), "Doe", "John", client1Email, "0601020304", "10 Rue A", clientPassHash);
                clientDAO.addClient(client1);
                System.out.println("Client 1 ajouté : " + client1.getNomComplet());
            } else {
                client1 = clientDAO.getClientByEmail(client1Email).get();
                System.out.println("Client 1 déjà existant : " + client1.getNomComplet());
            }

            // Charger les comptes existants du client si présents
            List<Compte> existingComptes = compteService.getCompteDAO().getComptesByClientId(client1.getId());
            if (existingComptes.isEmpty()) {
                compte1 = compteService.creerCompteCourant(client1, 1000.0, 200.0);
                compte2 = compteService.creerCompteEpargne(client1, 500.0, 1.5);
            } else {
                compte1 = existingComptes.get(0); // On suppose qu'il y a au moins un compte
                if (existingComptes.size() > 1) {
                    compte2 = existingComptes.get(1); // On suppose qu'il y a un deuxième
                } else {
                    compte2 = compteService.creerCompteEpargne(client1, 500.0, 1.5); // Si un seul, en créer un deuxième
                }
                System.out.println("Comptes du client 1 chargés.");
            }


            // Ajout/Vérification d'un autre client pour les virements
            String client2Email = "client2@example.com";
            if (!clientDAO.getClientByEmail(client2Email).isPresent()) {
                String client2PassHash = passwordManager.hasher("pass456");
                client2 = new Client("C-" + UUID.randomUUID().toString().substring(0, 8), "Smith", "Jane", client2Email, "0701020304", "20 Rue B", client2PassHash);
                clientDAO.addClient(client2);
                System.out.println("Client 2 ajouté : " + client2.getNomComplet());
            } else {
                client2 = clientDAO.getClientByEmail(client2Email).get();
                System.out.println("Client 2 déjà existant : " + client2.getNomComplet());
            }

            List<Compte> existingComptes2 = compteService.getCompteDAO().getComptesByClientId(client2.getId());
            if (existingComptes2.isEmpty()) {
                compte3 = compteService.creerCompteCourant(client2, 200.0, 50.0);
            } else {
                compte3 = existingComptes2.get(0);
                System.out.println("Compte du client 2 chargé.");
            }


            // Ajout/Vérification d'un admin de test
            String adminLogin = "superadmin";
            if (!adminDAO.getAdminByLogin(adminLogin).isPresent()) {
                String adminPassHash = passwordManager.hasher("adminpass");
                Admin testAdmin = new Admin("A-" + UUID.randomUUID().toString().substring(0, 8), adminLogin, adminPassHash);
                adminDAO.addAdmin(testAdmin);
                System.out.println("Admin de test ajouté : " + testAdmin.getLogin());
            } else {
                System.out.println("Admin de test déjà existant.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur de préparation de la DB : " + e.getMessage());
            e.printStackTrace();
            return; // Arrêter si la DB n'est pas prête
        } catch (CompteNonTrouveException e) {
            System.err.println("Erreur lors de la récupération des comptes : " + e.getMessage());
            e.printStackTrace();
            return;
        }


        // --- Tests d'authentification (déjà fonctionnels) ---
        System.out.println("\n--- Tests d'authentification ---");
        try {
            Client authClient = authService.authentifierClient("client1@example.com", "pass123");
            System.out.println("Authentification Client réussie : " + authClient.getNomComplet());
        } catch (AuthentificationException e) {
            System.out.println("Authentification Client échouée : " + e.getMessage());
        }

        // --- Tests des opérations sur les comptes ---
        System.out.println("\n--- Tests des opérations sur les comptes ---");
        if (compte1 != null) {
            try {
                System.out.println("Solde initial compte 1 (" + compte1.getNumero() + ") : " + compte1.getSolde());
                compteService.deposer(compte1.getNumero(), 50.0);
                System.out.println("Nouveau solde compte 1 après dépôt: " + compteService.getCompte(compte1.getNumero()).getSolde());

                compteService.retirer(compte1.getNumero(), 100.0);
                System.out.println("Nouveau solde compte 1 après retrait: " + compteService.getCompte(compte1.getNumero()).getSolde());

                // Tentative de retrait dépassant le découvert
                try {
                    compteService.retirer(compte1.getNumero(), 1500.0); // Devrait échouer
                } catch (SoldeInsuffisantException e) {
                    System.out.println("Retrait échoué (attendu) : " + e.getMessage());
                }

                if (compte2 != null) {
                    System.out.println("Solde initial compte 2 (" + compte2.getNumero() + ") : " + compte2.getSolde());
                    double interets = ((CompteEpargne)compte2).calculerInterets();
                    System.out.println("Intérêts calculés pour compte 2 : " + interets + " EUR");
                    compteService.deposer(compte2.getNumero(), interets); // Ajout des intérêts
                    System.out.println("Nouveau solde compte 2 après ajout intérêts: " + compteService.getCompte(compte2.getNumero()).getSolde());
                }

            } catch (CompteNonTrouveException | SoldeInsuffisantException | IllegalArgumentException | SQLException e) {
                System.err.println("Erreur lors des opérations sur comptes : " + e.getMessage());
                e.printStackTrace();
            }
        }

        // --- Tests de virement ---
        System.out.println("\n--- Tests de virement ---");
        // MODIFICATION ICI : Ajout de la vérification pour compte3
        if (compte1 != null && compte2 != null && compte3 != null) {
            try {
                System.out.println("Avant virement (C1->C2) : C1=" + compte1.getSolde() + ", C2=" + compte2.getSolde());
                transactionService.effectuerVirement(compte1.getNumero(), compte2.getNumero(), 50.0, "Transfert test");
                System.out.println("Après virement (C1->C2) : C1=" + compteService.getCompte(compte1.getNumero()).getSolde() + ", C2=" + compteService.getCompte(compte2.getNumero()).getSolde());

                // Test de virement échoué (solde insuffisant)
                try {
                    transactionService.effectuerVirement(compte1.getNumero(), compte3.getNumero(), 2000.0, "Virement trop grand");
                } catch (SoldeInsuffisantException e) {
                    System.out.println("Virement échoué (attendu) : " + e.getMessage());
                }

            } catch (CompteNonTrouveException | SoldeInsuffisantException | IllegalArgumentException | SQLException e) {
                System.err.println("Erreur lors des virements : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Impossible d'exécuter les tests de virement : un ou plusieurs comptes de test (compte1, compte2, compte3) n'ont pas été initialisés correctement.");
        }

        // --- Test de suppression de client et compte ---
        System.out.println("\n--- Tests de suppression ---");
        try {
            // Tentative de suppression du compte 1 (solde non nul)
            // On s'assure que compte1 n'est pas null avant de tenter la suppression
            if (compte1 != null) {
                boolean compte1Supprime = compteService.supprimerCompte(compte1.getNumero());
                System.out.println("Compte 1 (" + compte1.getNumero() + ") supprimé ? " + compte1Supprime);
            } else {
                System.out.println("Compte 1 n'est pas initialisé, impossible de tester la suppression.");
            }


            // Créer un compte temporaire avec solde nul pour la suppression
            Client tempClient = new Client("C-TEMP", "Temp", "Client", "temp@example.com", "000", "Temp", passwordManager.hasher("temp"));
            clientDAO.addClient(tempClient);
            Compte compteTemp = compteService.creerCompteCourant(tempClient, 0.0, 0.0);
            System.out.println("Compte temporaire créé : " + compteTemp.getNumero());

            boolean compteTempSupprime = compteService.supprimerCompte(compteTemp.getNumero());
            System.out.println("Compte temporaire (" + compteTemp.getNumero() + ") supprimé ? " + compteTempSupprime);

            // Vérifier que le client DAO peut lister les clients
            List<Client> allClients = clientDAO.getAllClients();
            System.out.println("Nombre total de clients dans la DB : " + allClients.size());
            System.out.println("Liste des clients :");
            allClients.forEach(client -> System.out.println("- " + client.getNomComplet() + " (ID: " + client.getId() + ")"));

            // Supprimer le client temporaire (après avoir supprimé ses comptes)
            if (compteTempSupprime) { // Si le compte temp a bien été supprimé
                // Dans un vrai système, il faudrait s'assurer que TOUS les comptes sont supprimés
                // avant de tenter de supprimer le client pour éviter les violations de FK.
                boolean clientTempSupprime = clientDAO.deleteClient(tempClient.getId());
                System.out.println("Client temporaire (" + tempClient.getId() + ") supprimé ? " + clientTempSupprime);
            }


        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        } catch (CompteNonTrouveException e) {
            System.err.println("Erreur (compte non trouvé) lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }

        // --- Test de l'historique des transactions ---
        System.out.println("\n--- Historique des transactions ---");
        if (compte1 != null) {
            try {
                List<Transaction> transactionsC1 = transactionService.getHistoriqueTransactions(compte1.getNumero());
                System.out.println("Historique pour le compte " + compte1.getNumero() + " (" + transactionsC1.size() + " transactions) :");
                transactionsC1.forEach(t -> System.out.println("  - [" + t.getDate() + "] " + t.getType() + ": " + t.getMontant() + " EUR - " + t.getDescription()));
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération de l'historique : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
