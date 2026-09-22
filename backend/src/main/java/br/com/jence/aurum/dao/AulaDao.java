package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Aula;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Aula.
 * Gerencia os módulos educacionais e de gamificação na tabela TB_AULA do Oracle Database.
 */
public class AulaDao implements Dao<Aula, Long> {

    @Override
    public void inserir(Aula aula) throws SQLException {
        String sql = "INSERT INTO TB_AULA (" +
                     "  NM_TITULO, DS_CONTEUDO, NR_ORDEM, QT_XP_RECOMPENSA" +
                     ") VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_AULA" });

            stmt.setString(1, aula.getTitulo());
            stmt.setString(2, aula.getConteudoHtml());
            stmt.setInt(3, aula.getOrdem());
            stmt.setInt(4, aula.getPontosXp());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                aula.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Aula aula) throws SQLException {
        String sql = "UPDATE TB_AULA SET " +
                     "  NM_TITULO = ?, " +
                     "  DS_CONTEUDO = ?, " +
                     "  NR_ORDEM = ?, " +
                     "  QT_XP_RECOMPENSA = ? " +
                     "WHERE ID_AULA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, aula.getTitulo());
            stmt.setString(2, aula.getConteudoHtml());
            stmt.setInt(3, aula.getOrdem());
            stmt.setInt(4, aula.getPontosXp());
            stmt.setLong(5, aula.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma aula encontrada para atualização com ID: " + aula.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_AULA WHERE ID_AULA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma aula encontrada para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public Aula buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_AULA, NM_TITULO, DS_CONTEUDO, NR_ORDEM, QT_XP_RECOMPENSA " +
                     "  FROM TB_AULA WHERE ID_AULA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearAula(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Aula> listarTodos() throws SQLException {
        String sql = "SELECT ID_AULA, NM_TITULO, DS_CONTEUDO, NR_ORDEM, QT_XP_RECOMPENSA " +
                     "  FROM TB_AULA ORDER BY NR_ORDEM";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Aula> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearAula(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private Aula mapearAula(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_AULA");
        String titulo = rs.getString("NM_TITULO");
        String conteudo = rs.getString("DS_CONTEUDO");
        int ordem = rs.getInt("NR_ORDEM");
        int xp = rs.getInt("QT_XP_RECOMPENSA");

        return new Aula(id, titulo, conteudo, ordem, xp);
    }
}
