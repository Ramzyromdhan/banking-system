-- banking_system_db.sql - Script de mise à jour pour la base de données
CREATE DATABASE IF NOT EXISTS banking_system_db;
USE banking_system_db;

-- Table pour les clients (aucune modification si déjà créée comme précédemment)
CREATE TABLE IF NOT EXISTS clients (
    id VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    mot_de_passe_hash VARCHAR(255) NOT NULL
    );

-- Table pour les comptes
-- Si elle existe déjà, vous devrez peut-être la DROPper et la recréer si les colonnes changent
-- ou utiliser ALTER TABLE pour ajouter/modifier des colonnes.
-- Pour simplifier ici, si vous avez des problèmes, supprimez la table et recréez-la.
-- DROP TABLE IF EXISTS comptes;
CREATE TABLE IF NOT EXISTS comptes (
                                       numero VARCHAR(50) PRIMARY KEY,
    solde DOUBLE NOT NULL DEFAULT 0.0,
    date_ouverture DATE NOT NULL,
    client_id VARCHAR(50) NOT NULL, -- Clé étrangère vers la table clients
    type_compte VARCHAR(20) NOT NULL, -- 'COURANT' ou 'EPARGNE'
    decouvert_autorise DOUBLE, -- Applicable seulement pour les comptes courants
    taux_interet DOUBLE,      -- Applicable seulement pour les comptes épargne
    FOREIGN KEY (client_id) REFERENCES clients(id)
    );

-- Table pour les administrateurs (aucune modification)
CREATE TABLE IF NOT EXISTS admins (
                                      id VARCHAR(50) PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL
    );

-- Nouvelle table pour les transactions
CREATE TABLE IF NOT EXISTS transactions (
                                            id VARCHAR(50) PRIMARY KEY,
    type_transaction VARCHAR(20) NOT NULL, -- 'DEBIT', 'CREDIT', 'TRANSFERT'
    montant DOUBLE NOT NULL,
    date_transaction DATETIME NOT NULL,
    description VARCHAR(255),
    compte_source_numero VARCHAR(50) NOT NULL, -- Numéro du compte source
    compte_destination_numero VARCHAR(50), -- Numéro du compte destination (nullable si type_transaction est DEBIT/CREDIT)
    FOREIGN KEY (compte_source_numero) REFERENCES comptes(numero),
    FOREIGN KEY (compte_destination_numero) REFERENCES comptes(numero)
    );
