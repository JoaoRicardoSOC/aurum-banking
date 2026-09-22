package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Carteira;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Carteira.
 * Gerencia as operações na tabela TB_CARTEIRA do Oracle Database.
 */
public class CarteiraDao implements Dao<Carteira, Long> {

    @Override
    public void inserir(Carteira carteira) throws SQLException {
        String sql = "INSERT INTO TB_CARTEIRA (DS_ENDERECO_DIGITAL, VL_SALDO_DISPONIVEL_BRL) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_CARTEIRA" });

            stmt.setString(1, carteira.getEnderecoDigital());
            stmt.setBigDecimal(2, carteira.getSaldoDisponivelBrl() != null ? carteira.getSaldoDisponivelBrl() : BigDecimal.ZERO);

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                carteira.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Carteira carteira) throws SQLException {
        String sql = "UPDATE TB_CARTEIRA SET DS_ENDERECO_DIGITAL = ?, VL_SALDO_DISPONIVEL_BRL = ? WHERE ID_CARTEIRA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carteira.getEnderecoDigital());
            stmt.setBigDecimal(2, carteira.getSaldoDisponivelBrl() != null ? carteira.getSaldoDisponivelBrl() : BigDecimal.ZERO);
            stmt.setLong(3, carteira.getId());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma carteira encontrada para atualização com ID: " + carteira.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    public void atualizarSaldo(Carteira carteira) throws SQLException {
        atualizar(carteira);
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_CARTEIRA WHERE ID_CARTEIRA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma carteira encontrada para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    public void excluir(long id) throws SQLException {
        excluir(Long.valueOf(id));
    }

    @Override
    public Carteira buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_CARTEIRA, DS_ENDERECO_DIGITAL, VL_SALDO_DISPONIVEL_BRL FROM TB_CARTEIRA WHERE ID_CARTEIRA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                long idCarteira = rs.getLong("ID_CARTEIRA");
                String endereco = rs.getString("DS_ENDERECO_DIGITAL");
                BigDecimal saldo = rs.getBigDecimal("VL_SALDO_DISPONIVEL_BRL");

                Carteira carteira = new Carteira(idCarteira, endereco);
                carteira.setSaldoDisponivelBrl(saldo);
                return carteira;
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public Carteira buscarPorId(long id) throws SQLException {
        return buscarPorId(Long.valueOf(id));
    }

    @Override
    public List<Carteira> listarTodos() throws SQLException {
        String sql = "SELECT ID_CARTEIRA, DS_ENDERECO_DIGITAL, VL_SALDO_DISPONIVEL_BRL FROM TB_CARTEIRA ORDER BY ID_CARTEIRA";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Carteira> carteiras = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                long idCarteira = rs.getLong("ID_CARTEIRA");
                String endereco = rs.getString("DS_ENDERECO_DIGITAL");
                BigDecimal saldo = rs.getBigDecimal("VL_SALDO_DISPONIVEL_BRL");

                Carteira c = new Carteira(idCarteira, endereco);
                c.setSaldoDisponivelBrl(saldo);
                carteiras.add(c);
            }
            return carteiras;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public List<Carteira> listarTodas() throws SQLException {
        return listarTodos();
    }
}
