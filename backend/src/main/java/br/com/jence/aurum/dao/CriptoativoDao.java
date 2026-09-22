package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Criptoativo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Criptoativo.
 * Gerencia o catálogo de criptomoedas na tabela TB_CRIPTOATIVO do Oracle Database.
 */
public class CriptoativoDao implements Dao<Criptoativo, Long> {

    @Override
    public void inserir(Criptoativo cripto) throws SQLException {
        String sql = "INSERT INTO TB_CRIPTOATIVO (" +
                     "  NM_CRIPTOATIVO, CD_SIGLA, DS_LOGO_URL, " +
                     "  VL_PRECO_ATUAL_BRL, PC_VARIACAO_24H, FL_ATIVO_NEGOCIACAO" +
                     ") VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_CRIPTOATIVO" });

            stmt.setString(1, cripto.getNome());
            stmt.setString(2, cripto.getSigla().toUpperCase());
            stmt.setString(3, cripto.getLogoUrl());
            stmt.setBigDecimal(4, cripto.getPrecoAtualBrl());
            stmt.setBigDecimal(5, cripto.getVariacao24h() != null ? cripto.getVariacao24h() : BigDecimal.ZERO);
            stmt.setString(6, cripto.isAtivoParaNegociacao() ? "S" : "N");

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                cripto.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Criptoativo cripto) throws SQLException {
        String sql = "UPDATE TB_CRIPTOATIVO SET " +
                     "  NM_CRIPTOATIVO = ?, " +
                     "  CD_SIGLA = ?, " +
                     "  DS_LOGO_URL = ?, " +
                     "  VL_PRECO_ATUAL_BRL = ?, " +
                     "  PC_VARIACAO_24H = ?, " +
                     "  FL_ATIVO_NEGOCIACAO = ?, " +
                     "  DH_ULTIMA_ATUALIZACAO = SYSTIMESTAMP " +
                     "WHERE ID_CRIPTOATIVO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, cripto.getNome());
            stmt.setString(2, cripto.getSigla().toUpperCase());
            stmt.setString(3, cripto.getLogoUrl());
            stmt.setBigDecimal(4, cripto.getPrecoAtualBrl());
            stmt.setBigDecimal(5, cripto.getVariacao24h() != null ? cripto.getVariacao24h() : BigDecimal.ZERO);
            stmt.setString(6, cripto.isAtivoParaNegociacao() ? "S" : "N");
            stmt.setLong(7, cripto.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum criptoativo encontrado para atualização com ID: " + cripto.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_CRIPTOATIVO WHERE ID_CRIPTOATIVO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum criptoativo encontrado para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public Criptoativo buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_CRIPTOATIVO, NM_CRIPTOATIVO, CD_SIGLA, DS_LOGO_URL, " +
                     "       VL_PRECO_ATUAL_BRL, PC_VARIACAO_24H, FL_ATIVO_NEGOCIACAO " +
                     "  FROM TB_CRIPTOATIVO WHERE ID_CRIPTOATIVO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCriptoativo(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public Criptoativo buscarPorSigla(String sigla) throws SQLException {
        String sql = "SELECT ID_CRIPTOATIVO, NM_CRIPTOATIVO, CD_SIGLA, DS_LOGO_URL, " +
                     "       VL_PRECO_ATUAL_BRL, PC_VARIACAO_24H, FL_ATIVO_NEGOCIACAO " +
                     "  FROM TB_CRIPTOATIVO WHERE CD_SIGLA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, sigla.toUpperCase().trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCriptoativo(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Criptoativo> listarTodos() throws SQLException {
        String sql = "SELECT ID_CRIPTOATIVO, NM_CRIPTOATIVO, CD_SIGLA, DS_LOGO_URL, " +
                     "       VL_PRECO_ATUAL_BRL, PC_VARIACAO_24H, FL_ATIVO_NEGOCIACAO " +
                     "  FROM TB_CRIPTOATIVO ORDER BY ID_CRIPTOATIVO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Criptoativo> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearCriptoativo(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private Criptoativo mapearCriptoativo(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_CRIPTOATIVO");
        String nome = rs.getString("NM_CRIPTOATIVO");
        String sigla = rs.getString("CD_SIGLA");
        String logoUrl = rs.getString("DS_LOGO_URL");
        BigDecimal preco = rs.getBigDecimal("VL_PRECO_ATUAL_BRL");
        BigDecimal variacao = rs.getBigDecimal("PC_VARIACAO_24H");
        String ativoStr = rs.getString("FL_ATIVO_NEGOCIACAO");

        Criptoativo c = new Criptoativo(id, nome, sigla, preco);
        c.setLogoUrl(logoUrl);
        c.setVariacao24h(variacao);
        c.setAtivoParaNegociacao("S".equalsIgnoreCase(ativoStr));
        return c;
    }
}
