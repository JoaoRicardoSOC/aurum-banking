package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Carteira;
import br.com.jence.aurum.model.Empresa;
import br.com.jence.aurum.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Empresa.
 * Gerencia as operações CRUD na tabela TB_EMPRESA do Oracle Database.
 */
public class EmpresaDao implements Dao<Empresa, Long> {

    private final UsuarioDao usuarioDao;
    private final CarteiraDao carteiraDao;

    public EmpresaDao() {
        this.usuarioDao = new UsuarioDao();
        this.carteiraDao = new CarteiraDao();
    }

    @Override
    public void inserir(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO TB_EMPRESA (" +
                     "  DS_RAZAO_SOCIAL, NR_CNPJ, DS_EMAIL_CORPORATIVO, " +
                     "  FL_STATUS_ATIVO, ID_USUARIO_MASTER, ID_CARTEIRA" +
                     ") VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_EMPRESA" });

            stmt.setString(1, empresa.getRazaoSocial());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEmailCorporativo());
            stmt.setString(4, empresa.isStatusAtivo() ? "S" : "N");

            if (empresa.getUsuarioMaster() != null && empresa.getUsuarioMaster().getId() != null) {
                stmt.setLong(5, empresa.getUsuarioMaster().getId());
            } else {
                throw new SQLException("O usuário master vinculado é obrigatório para persistir a Empresa.");
            }

            if (empresa.getCarteira() != null && empresa.getCarteira().getId() != null) {
                stmt.setLong(6, empresa.getCarteira().getId());
            } else {
                throw new SQLException("A carteira corporativa vinculada é obrigatória para persistir a Empresa.");
            }

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                empresa.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Empresa empresa) throws SQLException {
        String sql = "UPDATE TB_EMPRESA SET " +
                     "  DS_RAZAO_SOCIAL = ?, " +
                     "  NR_CNPJ = ?, " +
                     "  DS_EMAIL_CORPORATIVO = ?, " +
                     "  FL_STATUS_ATIVO = ?, " +
                     "  ID_USUARIO_MASTER = ?, " +
                     "  ID_CARTEIRA = ? " +
                     "WHERE ID_EMPRESA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, empresa.getRazaoSocial());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEmailCorporativo());
            stmt.setString(4, empresa.isStatusAtivo() ? "S" : "N");
            stmt.setLong(5, empresa.getUsuarioMaster().getId());
            stmt.setLong(6, empresa.getCarteira().getId());
            stmt.setLong(7, empresa.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma empresa encontrada para atualização com ID: " + empresa.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM TB_EMPRESA WHERE ID_EMPRESA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Nenhuma empresa encontrada para exclusão com ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    @Override
    public Empresa buscarPorId(Long id) throws SQLException {
        String sql = "SELECT ID_EMPRESA, DS_RAZAO_SOCIAL, NR_CNPJ, DS_EMAIL_CORPORATIVO, " +
                     "       FL_STATUS_ATIVO, ID_USUARIO_MASTER, ID_CARTEIRA, DH_CRIACAO " +
                     "  FROM TB_EMPRESA WHERE ID_EMPRESA = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEmpresa(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Empresa> listarTodos() throws SQLException {
        String sql = "SELECT ID_EMPRESA, DS_RAZAO_SOCIAL, NR_CNPJ, DS_EMAIL_CORPORATIVO, " +
                     "       FL_STATUS_ATIVO, ID_USUARIO_MASTER, ID_CARTEIRA, DH_CRIACAO " +
                     "  FROM TB_EMPRESA ORDER BY ID_EMPRESA";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Empresa> empresas = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                empresas.add(mapearEmpresa(rs));
            }
            return empresas;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    private Empresa mapearEmpresa(ResultSet rs) throws SQLException {
        Long idEmpresa = rs.getLong("ID_EMPRESA");
        String razaoSocial = rs.getString("DS_RAZAO_SOCIAL");
        String cnpj = rs.getString("NR_CNPJ");
        String emailCorp = rs.getString("DS_EMAIL_CORPORATIVO");
        String statusStr = rs.getString("FL_STATUS_ATIVO");
        boolean ativo = "S".equalsIgnoreCase(statusStr);
        Long idUsuarioMaster = rs.getLong("ID_USUARIO_MASTER");
        Long idCarteira = rs.getLong("ID_CARTEIRA");

        Usuario master = usuarioDao.buscarPorId(idUsuarioMaster);
        Carteira carteira = carteiraDao.buscarPorId(idCarteira);

        Empresa empresa = new Empresa(idEmpresa, razaoSocial, cnpj, emailCorp, master, carteira);
        empresa.setStatusAtivo(ativo);
        return empresa;
    }
}
