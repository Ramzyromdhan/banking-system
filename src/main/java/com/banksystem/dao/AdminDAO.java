// src/main/java/com/banksystem/dao/AdminDAO.java
package com.banksystem.dao;

import com.banksystem.model.Admin;
import com.banksystem.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class AdminDAO {

    /**
     * Enregistre un nouvel administrateur dans la base de données.
     *
     * @param admin L'objet Admin à enregistrer.
     * @return true si l'admin est ajouté avec succès, false sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public boolean addAdmin(Admin admin) throws SQLException {
        String sql = "INSERT INTO admins (id, login, mot_de_passe_hash) VALUES (?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, admin.getId());
            statement.setString(2, admin.getLogin());
            statement.setString(3, admin.getMotDePasseHash());

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
     * Récupère un administrateur par son login.
     * Utile pour l'authentification.
     *
     * @param login Le login de l'administrateur.
     * @return Un Optional contenant l'objet Admin si trouvé, ou un Optional.empty() sinon.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Optional<Admin> getAdminByLogin(String login) throws SQLException {
        String sql = "SELECT id, login, mot_de_passe_hash FROM admins WHERE login = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, login);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String motDePasseHash = resultSet.getString("mot_de_passe_hash");
                Admin admin = new Admin(id, login, motDePasseHash);
                return Optional.of(admin);
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
        return Optional.empty(); // Aucun administrateur trouvé
    }

    // Ajoutez d'autres méthodes CRUD si nécessaire :
    // public boolean updateAdmin(Admin admin) ...
    // public boolean deleteAdmin(String idAdmin) ...
    // public List<Admin> getAllAdmins() ...
}