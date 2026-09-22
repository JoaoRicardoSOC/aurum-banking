package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Empresa;
import br.com.jence.aurum.model.SolicitacaoDeTransacao;
import br.com.jence.aurum.model.Transacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade SolicitacaoDeTransacao.
 * Gerencia o processo de governança e multi-assinatura corporativa na tabela TB_SOLICITACAO_TRANSACAO.
 */
public class SolicitacaoTransacaoDao implements Dao<SolicitacaoDeTransacao, Long> {

    private final EmpresaDao empresaDao;
    private final TransacaoDao transacaoDao;

    public SolicitacaoTransacaoDao() {
        this.empresaDao = new EmpresaDao();
        this.transacaoDao = new TransacaoDao();
    }

    @Override
    public void inserir(SolicitacaoDeTransacao solicitacao) throws SQLException {
        String sql = "INSERT INTO TB_SOLICITACAO_TRANSACAO (" +
                     "  ID_EMPRESA, ID_TRANSACAO, NR_QUORUM_MINIMO, " +
                     "  DH_EXPIRACAO, ST_SOLICITACAO, DH_CRIACAO, DS_MOTIVO_REJEICAO" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_SOLICITACAO" });

            if (solicitacao.getEmpresa() == null || solicitacao.getEmpresa().getId() == null) {
                throw new SQLException("A empresa solicitante é obrigatória para a Solicitação de Transação.");
            }
            if (solicitacao.getTransacaoPendente() == null || solicitacao.getTransacaoPendente().getId() == null) {
                throw new SQLException("A transação pendente vinculada é obrigatória para a Solicitação.");
            }

            stmt.setLong(1, solicitacao.getEmpresa().getId());
            stmt.setLong(2, solicitacao.getTransacaoPendente().getId());
            stmt.setInt(3, solicitacao.getMinimoAprovacoesNecessarias());
            stmt.setTimestamp(4, Timestamp.valueOf(solicitacao.getDataHoraRegistro().plusDays(2))); // expiracao
            stmt.setString(5, solicitacao.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(solicitacao.getDataCriacao() != null ? solicitacao.getDataCriacao() : LocalDateTime.now()));
            stmt.setString(7, solicitacao.getMotivoRejeicao());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                solicitacao.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(SolicitacaoDeTransacao solicitacao) throws SQLException {
        String sql = "UPDATE TB_SOLICITACAO_TRANSACAO SET " +
                     "  ST_SOLICITACAO = ?, " +
                     "  NR_QUORUM_MINIMO = ?, " +
                     "  DS_MOTIVO_REJEICAO = ? " +
                     "WHERE ID_SOLICITACAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, solicitacao.getStatus().name());
            stmt.setInt(2, solicitacao.getMinimoAprovacoesNecessarias());
            stmt.setString(3, solicitacao.getMotivoRejeicao());
            stmt.setLong(4, solicitacao.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma solicitação encontrada para atualização com ID: " + solicitacao.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_SOLICITACAO_TRANSACAO WHERE ID_SOLICITACAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma solicitação encontrada para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public SolicitacaoDeTransacao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_SOLICITACAO, ID_EMPRESA, ID_TRANSACAO, NR_QUORUM_MINIMO, " +
                     "       DH_EXPIRACAO, ST_SOLICITACAO, DH_CRIACAO, DS_MOTIVO_REJEICAO " +
                     "  FROM TB_SOLICITACAO_TRANSACAO WHERE ID_SOLICITACAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearSolicitacao(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<SolicitacaoDeTransacao> listarTodos() throws SQLException {
        String sql = "SELECT ID_SOLICITACAO, ID_EMPRESA, ID_TRANSACAO, NR_QUORUM_MINIMO, " +
                     "       DH_EXPIRACAO, ST_SOLICITACAO, DH_CRIACAO, DS_MOTIVO_REJEICAO " +
                     "  FROM TB_SOLICITACAO_TRANSACAO ORDER BY ID_SOLICITACAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<SolicitacaoDeTransacao> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearSolicitacao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private SolicitacaoDeTransacao mapearSolicitacao(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_SOLICITACAO");
        Long idEmpresa = rs.getLong("ID_EMPRESA");
        Long idTransacao = rs.getLong("ID_TRANSACAO");
        int quorum = rs.getInt("NR_QUORUM_MINIMO");
        Timestamp dhExpSql = rs.getTimestamp("DH_EXPIRACAO");
        LocalDateTime dhExp = dhExpSql != null ? dhExpSql.toLocalDateTime() : LocalDateTime.now().plusDays(2);
        Timestamp dhCriacaoSql = rs.getTimestamp("DH_CRIACAO");
        LocalDateTime dhCriacao = dhCriacaoSql != null ? dhCriacaoSql.toLocalDateTime() : LocalDateTime.now();
        String statusStr = rs.getString("ST_SOLICITACAO");
        String motivo = rs.getString("DS_MOTIVO_REJEICAO");

        Empresa empresa = empresaDao.buscarPorId(idEmpresa);
        Transacao transacao = transacaoDao.buscarPorId(idTransacao);

        SolicitacaoDeTransacao solicitacao = new SolicitacaoDeTransacao(id, empresa, transacao, quorum, dhExp);
        solicitacao.setDataCriacao(dhCriacao);
        solicitacao.setStatus(SolicitacaoDeTransacao.StatusSolicitacao.valueOf(statusStr.toUpperCase()));
        solicitacao.setMotivoRejeicao(motivo);
        return solicitacao;
    }
}
