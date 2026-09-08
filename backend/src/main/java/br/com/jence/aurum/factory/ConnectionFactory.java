package br.com.jence.aurum.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fábrica de conexões JDBC para o banco de dados Oracle.
 * Padrão de Projeto: Factory Method.
 * 
 * Configurado para o ambiente acadêmico FIAP.
 */
public class ConnectionFactory {

    private static final String DRIVER_CLASS = "oracle.jdbc.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String DB_USER = "rm563609";
    private static final String DB_PASS = "021006";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC da Oracle não encontrado no classpath: " + e.getMessage());
        }
    }

    // Construtor privado para evitar instanciação direta da Factory
    private ConnectionFactory() {}

    /**
     * Obtém uma nova conexão ativa com o banco de dados Oracle.
     *
     * @return java.sql.Connection
     * @throws SQLException caso ocorra falha de autenticação, rede ou conexão
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            System.err.println("Erro ao estabelecer conexão com Oracle Database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Utilitário para fechar com segurança os recursos JDBC abertos.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar ResultSet: " + e.getMessage());
        }

        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar Statement: " + e.getMessage());
        }

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar Connection: " + e.getMessage());
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    public static void close(Connection conn) {
        close(conn, null, null);
    }
}
