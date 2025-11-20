package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Classe utilitaire responsable de la gestion de la connexion à la base de données.
 * Elle fournit une méthode statique permettant d'établir une connexion JDBC
 * à la base MySQL utilisée par l'application.
 */
public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/BDReservation";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Établit et retourne une connexion à la base de données MySQL.
     *
     * @return une connexion JDBC active.
     * @throws SQLException si la connexion ne peut pas être établie
     * (serveur inaccessible,identifiants incorrects, base inexistante, etc.).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @FunctionalInterface
    public interface SQLTransaction<T> {
        T apply(Connection conn) throws Exception;
    }

    /**
     * Run the given work inside a JDBC transaction. Commits on success, rolls back on error,
     * and rethrows the original exception.
     */
    public static <T> T runInTransaction(SQLTransaction<T> work) throws Exception {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);
                T result = work.apply(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw e;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }
}

