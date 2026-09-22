package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Carteira;
import br.com.jence.aurum.model.Criptoativo;
import br.com.jence.aurum.model.Transacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Transacao.
 * Gerencia o livro-razão contábil e transacional na tabela TB_TRANSACAO do Oracle Database.
 */
public class TransacaoDao implements Dao<Transacao, Long> {

    private final CarteiraDao carteiraDao;
    private final CriptoativoDao criptoativoDao;

    public TransacaoDao() {
        this.carteiraDao = new CarteiraDao();
        this.criptoativoDao = new CriptoativoDao();
    }

    @Override
    public void inserir(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO TB_TRANSACAO (" +
                     "  TP_TRANSACAO, ST_TRANSACAO, DH_TRANSACAO, " +
                     "  VL_MOVIMENTADO_BRL, QT_CRIPTO, VL_COTACAO_MOMENTO, " +
                     "  VL_TAXA_OPERACAO, ID_CARTEIRA, ID_CRIPTOATIVO" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_TRANSACAO" });

            stmt.setString(1, transacao.getTipoTransacao().name());
            stmt.setString(2, transacao.getStatus().name());
            stmt.setTimestamp(3, transacao.getDataHora() != null ? Timestamp.valueOf(transacao.getDataHora()) : new Timestamp(System.currentTimeMillis()));
            stmt.setBigDecimal(4, transacao.getValorMovimentadoBrl());
            stmt.setBigDecimal(5, transacao.getQuantidadeCripto() != null ? transacao.getQuantidadeCripto() : BigDecimal.ZERO);
            stmt.setBigDecimal(6, transacao.getCotacaoNoMomento() != null ? transacao.getCotacaoNoMomento() : BigDecimal.ZERO);
            stmt.setBigDecimal(7, transacao.getTaxaOperacao() != null ? transacao.getTaxaOperacao() : BigDecimal.ZERO);

            if (transacao.getCarteiraVinculada() != null && transacao.getCarteiraVinculada().getId() != null) {
                stmt.setLong(8, transacao.getCarteiraVinculada().getId());
            } else {
                throw new SQLException("A carteira vinculada é obrigatória para persistir a Transação.");
            }

            if (transacao.getMoedaEnvolvida() != null && transacao.getMoedaEnvolvida().getId() != null) {
                stmt.setLong(9, transacao.getMoedaEnvolvida().getId());
            } else {
                stmt.setNull(9, Types.NUMERIC);
            }

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                transacao.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Transacao transacao) throws SQLException {
        String sql = "UPDATE TB_TRANSACAO SET " +
                     "  ST_TRANSACAO = ?, " +
                     "  VL_MOVIMENTADO_BRL = ?, " +
                     "  QT_CRIPTO = ?, " +
                     "  VL_COTACAO_MOMENTO = ?, " +
                     "  VL_TAXA_OPERACAO = ? " +
                     "WHERE ID_TRANSACAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, transacao.getStatus().name());
            stmt.setBigDecimal(2, transacao.getValorMovimentadoBrl());
            stmt.setBigDecimal(3, transacao.getQuantidadeCripto());
            stmt.setBigDecimal(4, transacao.getCotacaoNoMomento());
            stmt.setBigDecimal(5, transacao.getTaxaOperacao());
            stmt.setLong(6, transacao.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma transação encontrada para atualização com ID: " + transacao.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_TRANSACAO WHERE ID_TRANSACAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma transação encontrada para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public Transacao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_TRANSACAO, TP_TRANSACAO, ST_TRANSACAO, DH_TRANSACAO, " +
                     "       VL_MOVIMENTADO_BRL, QT_CRIPTO, VL_COTACAO_MOMENTO, " +
                     "       VL_TAXA_OPERACAO, ID_CARTEIRA, ID_CRIPTOATIVO " +
                     "  FROM TB_TRANSACAO WHERE ID_TRANSACAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearTransacao(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Transacao> listarTodos() throws SQLException {
        String sql = "SELECT ID_TRANSACAO, TP_TRANSACAO, ST_TRANSACAO, DH_TRANSACAO, " +
                     "       VL_MOVIMENTADO_BRL, QT_CRIPTO, VL_COTACAO_MOMENTO, " +
                     "       VL_TAXA_OPERACAO, ID_CARTEIRA, ID_CRIPTOATIVO " +
                     "  FROM TB_TRANSACAO ORDER BY ID_TRANSACAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transacao> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearTransacao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public List<Transacao> listarPorCarteira(Long idCarteira) throws SQLException {
        String sql = "SELECT ID_TRANSACAO, TP_TRANSACAO, ST_TRANSACAO, DH_TRANSACAO, " +
                     "       VL_MOVIMENTADO_BRL, QT_CRIPTO, VL_COTACAO_MOMENTO, " +
                     "       VL_TAXA_OPERACAO, ID_CARTEIRA, ID_CRIPTOATIVO " +
                     "  FROM TB_TRANSACAO WHERE ID_CARTEIRA = ? ORDER BY ID_TRANSACAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transacao> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idCarteira);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearTransacao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private Transacao mapearTransacao(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_TRANSACAO");
        String tipoStr = rs.getString("TP_TRANSACAO");
        String statusStr = rs.getString("ST_TRANSACAO");
        Timestamp dhSql = rs.getTimestamp("DH_TRANSACAO");
        LocalDateTime dh = dhSql != null ? dhSql.toLocalDateTime() : LocalDateTime.now();
        BigDecimal valorBrl = rs.getBigDecimal("VL_MOVIMENTADO_BRL");
        BigDecimal qtCripto = rs.getBigDecimal("QT_CRIPTO");
        BigDecimal cotacao = rs.getBigDecimal("VL_COTACAO_MOMENTO");
        BigDecimal taxa = rs.getBigDecimal("VL_TAXA_OPERACAO");
        Long idCarteira = rs.getLong("ID_CARTEIRA");
        long idCriptoLong = rs.getLong("ID_CRIPTOATIVO");
        Long idCripto = rs.wasNull() ? null : idCriptoLong;

        Carteira carteira = carteiraDao.buscarPorId(idCarteira);
        Criptoativo moeda = (idCripto != null) ? criptoativoDao.buscarPorId(idCripto) : null;

        Transacao.TipoTransacao tipo = Transacao.TipoTransacao.valueOf(tipoStr.toUpperCase());
        Transacao t = new Transacao(id, tipo, valorBrl, qtCripto, cotacao, taxa, carteira, moeda);
        t.setDataHora(dh);
        t.setStatus(Transacao.StatusTransacao.valueOf(statusStr.toUpperCase()));
        return t;
    }
}
