package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Empresa;
import br.com.jence.aurum.model.Guardiao;
import br.com.jence.aurum.model.Usuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Guardiao.
 * Gerencia os guardiões corporativos multi-assinatura na tabela TB_GUARDIAO do Oracle Database.
 */
public class GuardiaoDao implements Dao<Guardiao, Long> {

    private final UsuarioDao usuarioDao;
    private final EmpresaDao empresaDao;

    public GuardiaoDao() {
        this.usuarioDao = new UsuarioDao();
        this.empresaDao = new EmpresaDao();
    }

    @Override
    public void inserir(Guardiao guardiao) throws SQLException {
        String sql = "INSERT INTO TB_GUARDIAO (" +
                     "  ID_USUARIO, ID_EMPRESA, DT_NOMEACAO, FL_ATIVO" +
                     ") VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_GUARDIAO" });

            if (guardiao.getUsuarioResponsavel() == null || guardiao.getUsuarioResponsavel().getId() == null) {
                throw new SQLException("O usuário responsável é obrigatório para persistir o Guardião.");
            }
            if (guardiao.getEmpresaProtegida() == null || guardiao.getEmpresaProtegida().getId() == null) {
                throw new SQLException("A empresa protegida é obrigatória para persistir o Guardião.");
            }

            stmt.setLong(1, guardiao.getUsuarioResponsavel().getId());
            stmt.setLong(2, guardiao.getEmpresaProtegida().getId());
            stmt.setDate(3, Date.valueOf(guardiao.getDataNomeacao() != null ? guardiao.getDataNomeacao() : LocalDate.now()));
            stmt.setString(4, guardiao.isAtivo() ? "S" : "N");

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                guardiao.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Guardiao guardiao) throws SQLException {
        String sql = "UPDATE TB_GUARDIAO SET " +
                     "  FL_ATIVO = ?, " +
                     "  DT_NOMEACAO = ? " +
                     "WHERE ID_GUARDIAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, guardiao.isAtivo() ? "S" : "N");
            stmt.setDate(2, Date.valueOf(guardiao.getDataNomeacao()));
            stmt.setLong(3, guardiao.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum guardião encontrado para atualização com ID: " + guardiao.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_GUARDIAO WHERE ID_GUARDIAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum guardião encontrado para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public Guardiao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_GUARDIAO, ID_USUARIO, ID_EMPRESA, DT_NOMEACAO, FL_ATIVO " +
                     "  FROM TB_GUARDIAO WHERE ID_GUARDIAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearGuardiao(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public List<Guardiao> listarPorEmpresa(Long idEmpresa) throws SQLException {
        String sql = "SELECT ID_GUARDIAO, ID_USUARIO, ID_EMPRESA, DT_NOMEACAO, FL_ATIVO " +
                     "  FROM TB_GUARDIAO WHERE ID_EMPRESA = ? ORDER BY ID_GUARDIAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Guardiao> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idEmpresa);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearGuardiao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Guardiao> listarTodos() throws SQLException {
        String sql = "SELECT ID_GUARDIAO, ID_USUARIO, ID_EMPRESA, DT_NOMEACAO, FL_ATIVO " +
                     "  FROM TB_GUARDIAO ORDER BY ID_GUARDIAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Guardiao> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearGuardiao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private Guardiao mapearGuardiao(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_GUARDIAO");
        Long idUsuario = rs.getLong("ID_USUARIO");
        Long idEmpresa = rs.getLong("ID_EMPRESA");
        Date dtNomeacaoSql = rs.getDate("DT_NOMEACAO");
        LocalDate dtNomeacao = dtNomeacaoSql != null ? dtNomeacaoSql.toLocalDate() : LocalDate.now();
        String ativoStr = rs.getString("FL_ATIVO");

        Usuario usuario = usuarioDao.buscarPorId(idUsuario);
        Empresa empresa = empresaDao.buscarPorId(idEmpresa);

        return new Guardiao(id, usuario, empresa, dtNomeacao, "S".equalsIgnoreCase(ativoStr));
    }
}
