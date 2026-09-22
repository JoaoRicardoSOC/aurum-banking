package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Criptoativo;
import br.com.jence.aurum.model.PosicaoCripto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade PosicaoCripto.
 * Gerencia os saldos custodiados de criptomoedas na tabela TB_POSICAO_CRIPTO do Oracle Database.
 */
public class PosicaoCriptoDao implements Dao<PosicaoCripto, Long> {

    private final CriptoativoDao criptoativoDao;

    public PosicaoCriptoDao() {
        this.criptoativoDao = new CriptoativoDao();
    }

    @Override
    public void inserir(PosicaoCripto posicao) throws SQLException {
        String sql = "INSERT INTO TB_POSICAO_CRIPTO (" +
                     "  ID_CARTEIRA, ID_CRIPTOATIVO, QT_TOTAL" +
                     ") VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_POSICAO" });

            if (posicao.getIdCarteira() == null) {
                throw new SQLException("O ID da carteira vinculada é obrigatório para persistir PosicaoCripto.");
            }
            if (posicao.getMoeda() == null || posicao.getMoeda().getId() == null) {
                throw new SQLException("O criptoativo vinculado é obrigatório para persistir PosicaoCripto.");
            }

            stmt.setLong(1, posicao.getIdCarteira());
            stmt.setLong(2, posicao.getMoeda().getId());
            stmt.setBigDecimal(3, posicao.getQuantidadeTotal() != null ? posicao.getQuantidadeTotal() : BigDecimal.ZERO);

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                posicao.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(PosicaoCripto posicao) throws SQLException {
        String sql = "UPDATE TB_POSICAO_CRIPTO SET " +
                     "  QT_TOTAL = ?, " +
                     "  DH_ULTIMA_MOVIMENTACAO = SYSTIMESTAMP " +
                     "WHERE ID_POSICAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setBigDecimal(1, posicao.getQuantidadeTotal());
            stmt.setLong(2, posicao.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma posição cripto encontrada para atualização com ID: " + posicao.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_POSICAO_CRIPTO WHERE ID_POSICAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma posição cripto encontrada para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public PosicaoCripto buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_POSICAO, ID_CARTEIRA, ID_CRIPTOATIVO, QT_TOTAL, DH_ULTIMA_MOVIMENTACAO " +
                     "  FROM TB_POSICAO_CRIPTO WHERE ID_POSICAO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPosicao(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    public List<PosicaoCripto> listarPorCarteira(Long idCarteira) throws SQLException {
        String sql = "SELECT ID_POSICAO, ID_CARTEIRA, ID_CRIPTOATIVO, QT_TOTAL, DH_ULTIMA_MOVIMENTACAO " +
                     "  FROM TB_POSICAO_CRIPTO WHERE ID_CARTEIRA = ? ORDER BY ID_POSICAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PosicaoCripto> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idCarteira);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearPosicao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<PosicaoCripto> listarTodos() throws SQLException {
        String sql = "SELECT ID_POSICAO, ID_CARTEIRA, ID_CRIPTOATIVO, QT_TOTAL, DH_ULTIMA_MOVIMENTACAO " +
                     "  FROM TB_POSICAO_CRIPTO ORDER BY ID_POSICAO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PosicaoCripto> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearPosicao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private PosicaoCripto mapearPosicao(ResultSet rs) throws SQLException {
        Long idPosicao = rs.getLong("ID_POSICAO");
        Long idCarteira = rs.getLong("ID_CARTEIRA");
        Long idCripto = rs.getLong("ID_CRIPTOATIVO");
        BigDecimal qtTotal = rs.getBigDecimal("QT_TOTAL");

        Criptoativo cripto = criptoativoDao.buscarPorId(idCripto);
        return new PosicaoCripto(idPosicao, idCarteira, cripto, qtTotal);
    }
}
