package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Carteira;
import br.com.jence.aurum.model.CofreTemporal;
import br.com.jence.aurum.model.Criptoativo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade CofreTemporal.
 * Gerencia os cofres de bloqueio temporário de ativos na tabela TB_COFRE_TEMPORAL do Oracle Database.
 */
public class CofreTemporalDao implements Dao<CofreTemporal, Long> {

    private final CarteiraDao carteiraDao;
    private final CriptoativoDao criptoativoDao;

    public CofreTemporalDao() {
        this.carteiraDao = new CarteiraDao();
        this.criptoativoDao = new CriptoativoDao();
    }

    @Override
    public void inserir(CofreTemporal cofre) throws SQLException {
        String sql = "INSERT INTO TB_COFRE_TEMPORAL (" +
                     "  NM_OBJETIVO, DT_CRIACAO, DT_LIBERACAO, QT_BLOQUEADA, " +
                     "  ST_COFRE, ID_CARTEIRA, ID_CRIPTOATIVO, FL_DISPONIVEL" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_COFRE" });

            stmt.setString(1, cofre.getNome());
            stmt.setDate(2, Date.valueOf(cofre.getDataCriacao() != null ? cofre.getDataCriacao() : LocalDate.now()));
            stmt.setDate(3, Date.valueOf(cofre.getDataLiberacao()));
            stmt.setBigDecimal(4, cofre.getQuantidadeBloqueada());
            stmt.setString(5, cofre.getStatus().name());

            if (cofre.getCarteiraOrigem() != null && cofre.getCarteiraOrigem().getId() != null) {
                stmt.setLong(6, cofre.getCarteiraOrigem().getId());
            } else {
                throw new SQLException("A carteira de origem é obrigatória para o CofreTemporal.");
            }

            if (cofre.getAtivoBloqueado() != null && cofre.getAtivoBloqueado().getId() != null) {
                stmt.setLong(7, cofre.getAtivoBloqueado().getId());
            } else {
                throw new SQLException("O criptoativo bloqueado é obrigatório para o CofreTemporal.");
            }

            stmt.setString(8, cofre.isDisponivel() ? "S" : "N");

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                cofre.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(CofreTemporal cofre) throws SQLException {
        String sql = "UPDATE TB_COFRE_TEMPORAL SET " +
                     "  NM_OBJETIVO = ?, " +
                     "  DT_LIBERACAO = ?, " +
                     "  QT_BLOQUEADA = ?, " +
                     "  ST_COFRE = ?, " +
                     "  FL_DISPONIVEL = ? " +
                     "WHERE ID_COFRE = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, cofre.getNome());
            stmt.setDate(2, Date.valueOf(cofre.getDataLiberacao()));
            stmt.setBigDecimal(3, cofre.getQuantidadeBloqueada());
            stmt.setString(4, cofre.getStatus().name());
            stmt.setString(5, cofre.isDisponivel() ? "S" : "N");
            stmt.setLong(6, cofre.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum cofre temporal encontrado para atualização com ID: " + cofre.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_COFRE_TEMPORAL WHERE ID_COFRE = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhum cofre temporal encontrado para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public CofreTemporal buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_COFRE, NM_OBJETIVO, DT_CRIACAO, DT_LIBERACAO, " +
                     "       QT_BLOQUEADA, ST_COFRE, ID_CARTEIRA, ID_CRIPTOATIVO, FL_DISPONIVEL " +
                     "  FROM TB_COFRE_TEMPORAL WHERE ID_COFRE = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCofre(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<CofreTemporal> listarTodos() throws SQLException {
        String sql = "SELECT ID_COFRE, NM_OBJETIVO, DT_CRIACAO, DT_LIBERACAO, " +
                     "       QT_BLOQUEADA, ST_COFRE, ID_CARTEIRA, ID_CRIPTOATIVO, FL_DISPONIVEL " +
                     "  FROM TB_COFRE_TEMPORAL ORDER BY ID_COFRE";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<CofreTemporal> lista = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearCofre(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private CofreTemporal mapearCofre(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_COFRE");
        String nomeObjetivo = rs.getString("NM_OBJETIVO");
        Date dtCriacaoSql = rs.getDate("DT_CRIACAO");
        LocalDate dtCriacao = dtCriacaoSql != null ? dtCriacaoSql.toLocalDate() : LocalDate.now();
        Date dtLiberacaoSql = rs.getDate("DT_LIBERACAO");
        LocalDate dtLiberacao = dtLiberacaoSql != null ? dtLiberacaoSql.toLocalDate() : LocalDate.now().plusDays(1);
        BigDecimal qtBloqueada = rs.getBigDecimal("QT_BLOQUEADA");
        String statusStr = rs.getString("ST_COFRE");
        Long idCarteira = rs.getLong("ID_CARTEIRA");
        Long idCripto = rs.getLong("ID_CRIPTOATIVO");
        String disponivelStr = rs.getString("FL_DISPONIVEL");

        Carteira carteira = carteiraDao.buscarPorId(idCarteira);
        Criptoativo cripto = criptoativoDao.buscarPorId(idCripto);

        CofreTemporal cofre = new CofreTemporal(id, nomeObjetivo, dtLiberacao, qtBloqueada, cripto, carteira);
        cofre.setDataCriacao(dtCriacao);
        cofre.setStatus(CofreTemporal.StatusCofre.valueOf(statusStr.toUpperCase()));
        cofre.setDisponivel("S".equalsIgnoreCase(disponivelStr));
        return cofre;
    }
}
