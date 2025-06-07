CREATE DATABASE IF NOT EXISTS banking_system_db;
USE banking_system_db;

USE banking_system_db;

CREATE TABLE IF NOT EXISTS clients (
    id VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    mot_de_passe_hash VARCHAR(255) NOT NULL
);

-- Table pour les comptes (Compte abstraite)
-- Les types de comptes (courant, épargne) seront gérés par une colonne 'type_compte'
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

-- Table pour les administrateurs
CREATE TABLE IF NOT EXISTS admins (
    id VARCHAR(50) PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL
);

-- Table pour les transactions (vous pouvez l'ajouter plus tard)
-- CREATE TABLE IF NOT EXISTS transactions ( ... );