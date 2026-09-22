package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Aula;
import br.com.jence.aurum.model.ProgressoUsuarioAula;
import br.com.jence.aurum.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade ProgressoUsuarioAula.
 * Gerencia o acompanhamento pedagógico na tabela TB_PROGRESSO_AULA do Oracle Database.
 */
public class ProgressoUsuarioAulaDao implements Dao<ProgressoUsuarioAula, Long> {

    private final UsuarioDao usuarioDao;
    private final AulaDao aulaDao;

    public ProgressoUsuarioAulaDao() {
        this.usuarioDao = new UsuarioDao();
        this.aulaDao = new AulaDao();
    }

    @Override
    public void inserir(ProgressoUsuarioAula progresso) throws SQLException {
        String sql = "INSERT INTO TB_PROGRESSO_AULA (" +
                     "  ID_USUARIO, ID_AULA, FL_CONCLUIDA, DT_CONCLUSAO" +
                     ") VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_PROGRESSO" });

            if (progresso.getAluno() == null || progresso.getAluno().getId() == null) {
                throw new SQLException("O aluno (Usuário) é obrigatório para registrar o progresso da aula.");
            }
            if (progresso.getAulaAssistida() == null || progresso.getAulaAssistida().getId() == null) {
                throw new SQLException("A aula assistida é obrigatória para registrar o progresso.");
            }

            stmt.setLong(1, progresso.getAluno().getId());
            stmt.setLong(2, progresso.getAulaAssistida().getId());
            stmt.setString(3, progresso.isConcluida() ? "S" : "N");
            stmt.setTimestamp(4, progresso.getDataConclusao() != null ? Timestamp.valueOf(progresso.getDataConclusao()) : null);

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                progresso.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(ProgressoUsuarioAula progresso) throws SQLException {
        String sql = "UPDATE TB_PROGRESSO_AULA SET " +
                     "  FL_CONCLUIDA = ?, " +
                     "  DT_CONCLUSAO = ? " +
                     "WHERE ID_PROGRESSO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, progresso.isConcluida() ? "S" : "N");
            stmt.setTimestamp(2, progresso.getDataConclusao() != null ? Timestamp.valueOf(progresso.getDataConclusao()) : null);
            stmt.setLong(3, progresso.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum progresso encontrado para atualização com ID: " + progresso.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_PROGRESSO_AULA WHERE ID_PROGRESSO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum progresso encontrado para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public ProgressoUsuarioAula buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_PROGRESSO, ID_USUARIO, ID_AULA, FL_CONCLUIDA, DT_CONCLUSAO " +
                     "  FROM TB_PROGRESSO_AULA WHERE ID_PROGRESSO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearProgresso(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public List<ProgressoUsuarioAula> listarPorUsuario(Long idUsuario) throws SQLException {
        String sql = "SELECT ID_PROGRESSO, ID_USUARIO, ID_AULA, FL_CONCLUIDA, DT_CONCLUSAO " +
                     "  FROM TB_PROGRESSO_AULA WHERE ID_USUARIO = ? ORDER BY ID_PROGRESSO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ProgressoUsuarioAula> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearProgresso(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<ProgressoUsuarioAula> listarTodos() throws SQLException {
        String sql = "SELECT ID_PROGRESSO, ID_USUARIO, ID_AULA, FL_CONCLUIDA, DT_CONCLUSAO " +
                     "  FROM TB_PROGRESSO_AULA ORDER BY ID_PROGRESSO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ProgressoUsuarioAula> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearProgresso(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private ProgressoUsuarioAula mapearProgresso(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_PROGRESSO");
        Long idUsuario = rs.getLong("ID_USUARIO");
        Long idAula = rs.getLong("ID_AULA");
        String concluidaStr = rs.getString("FL_CONCLUIDA");
        Timestamp dtConclusaoSql = rs.getTimestamp("DT_CONCLUSAO");
        LocalDateTime dtConclusao = dtConclusaoSql != null ? dtConclusaoSql.toLocalDateTime() : null;

        Usuario aluno = usuarioDao.buscarPorId(idUsuario);
        Aula aula = aulaDao.buscarPorId(idAula);

        return new ProgressoUsuarioAula(id, aluno, aula, "S".equalsIgnoreCase(concluidaStr), dtConclusao);
    }
}
