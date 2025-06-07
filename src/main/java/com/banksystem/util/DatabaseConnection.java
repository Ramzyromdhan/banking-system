// src/main/java/com/banksystem/util/DatabaseConnection.java
package com.banksystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/banking_system_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Établit et retourne une connexion à la base de données.
     *
     * @return Un objet Connection pour interagir avec la base de données.
     * @throws SQLException Si une erreur de connexion à la base de données se produit.
     */
    public static Connection getConnection() throws SQLException {
        // La ligne suivante n'est plus strictement nécessaire avec JDBC 4.0+
        // car le pilote se charge automatiquement, mais peut être utile pour la clarté.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Le pilote JDBC MySQL n'a pas été trouvé.");
            throw new SQLException("Pilote JDBC manquant", e);
        }
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Ferme une connexion à la base de données en toute sécurité.
     *
     * @param connection La connexion à fermer.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion à la base de données : " + e.getMessage());
            }
        }
    }
}