package br.com.jence.aurum.dao;

import br.com.jence.aurum.factory.ConnectionFactory;
import br.com.jence.aurum.model.Carteira;
import br.com.jence.aurum.model.Usuario;
import br.com.jence.aurum.model.Usuario.ModoInterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Usuario.
 * Gerencia as operações CRUD na tabela TB_USUARIO do Oracle Database.
 */
public class UsuarioDao {

    /**
     * DML INSERT: Insere um novo usuário na tabela TB_USUARIO via PreparedStatement.
     *
     * @param usuario Instância de Usuario a ser persistida
     * @throws SQLException Em caso de erro na execução SQL
     */
    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO TB_USUARIO (" +
                     "  NM_COMPLETO, NR_CPF, DS_EMAIL, DS_SENHA_HASH, " +
                     "  DT_NASCIMENTO, DS_MODO_INTERFACE, VL_LIMITE_MENSAL, " +
                     "  FL_KYC_APROVADO, ID_CARTEIRA" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql, new String[] { "ID_USUARIO" });

            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenhaHash());
            stmt.setDate(5, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(6, usuario.getModoInterface() != null ? usuario.getModoInterface().name() : "INICIANTE");
            stmt.setBigDecimal(7, usuario.getLimiteOperacionalMensal());
            stmt.setString(8, usuario.isKycAprovado() ? "S" : "N");

            Long idCarteira = (usuario.getCarteira() != null) ? usuario.getCarteira().getId() : null;
            if (idCarteira != null) {
                stmt.setLong(9, idCarteira);
            } else {
                throw new SQLException("A carteira vinculada ao usuário é obrigatória para a persistência.");
            }

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                usuario.setId(rs.getLong(1));
            }
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    /**
     * DML UPDATE: Atualiza os campos de um usuário existente na tabela TB_USUARIO.
     *
     * @param usuario Instância com os dados atualizados
     * @throws SQLException Em caso de erro na execução SQL
     */
    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE TB_USUARIO SET " +
                     "  NM_COMPLETO = ?, " +
                     "  NR_CPF = ?, " +
                     "  DS_EMAIL = ?, " +
                     "  DS_SENHA_HASH = ?, " +
                     "  DT_NASCIMENTO = ?, " +
                     "  DS_MODO_INTERFACE = ?, " +
                     "  VL_LIMITE_MENSAL = ?, " +
                     "  FL_KYC_APROVADO = ?, " +
                     "  ID_CARTEIRA = ? " +
                     "WHERE ID_USUARIO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenhaHash());
            stmt.setDate(5, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(6, usuario.getModoInterface() != null ? usuario.getModoInterface().name() : "INICIANTE");
            stmt.setBigDecimal(7, usuario.getLimiteOperacionalMensal());
            stmt.setString(8, usuario.isKycAprovado() ? "S" : "N");

            Long idCarteira = (usuario.getCarteira() != null) ? usuario.getCarteira().getId() : null;
            if (idCarteira != null) {
                stmt.setLong(9, idCarteira);
            } else {
                stmt.setNull(9, Types.NUMERIC);
            }

            stmt.setLong(10, usuario.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhum usuário encontrado para atualização com o ID: " + usuario.getId());
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    /**
     * DML DELETE: Remove um usuário pelo seu ID (long).
     *
     * @param id Identificador do usuário
     * @throws SQLException Em caso de erro na execução SQL
     */
    public void excluir(long id) throws SQLException {
        String sql = "DELETE FROM TB_USUARIO WHERE ID_USUARIO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhum usuário encontrado para exclusão com o ID: " + id);
            }
        } finally {
            ConnectionFactory.close(conn, stmt);
        }
    }

    /**
     * DML DELETE: Sobrecarga para exclusão por ID inteiro.
     *
     * @param id Identificador do usuário
     * @throws SQLException Em caso de erro na execução SQL
     */
    public void excluir(int id) throws SQLException {
        excluir((long) id);
    }

    /**
     * DML SELECT: Busca um usuário pelo seu ID (long) com JOIN na Carteira.
     *
     * @param id Identificador do usuário
     * @return Instância de Usuario encontrada ou null se inexistente
     * @throws SQLException Em caso de erro na execução SQL
     */
    public Usuario buscarPorId(long id) throws SQLException {
        String sql = "SELECT u.ID_USUARIO, u.NM_COMPLETO, u.NR_CPF, u.DS_EMAIL, u.DS_SENHA_HASH, " +
                     "       u.DT_NASCIMENTO, u.DS_MODO_INTERFACE, u.VL_LIMITE_MENSAL, " +
                     "       u.FL_KYC_APROVADO, u.ID_CARTEIRA, " +
                     "       c.DS_ENDERECO_DIGITAL, c.VL_SALDO_DISPONIVEL_BRL " +
                     "  FROM TB_USUARIO u " +
                     "  INNER JOIN TB_CARTEIRA c ON u.ID_CARTEIRA = c.ID_CARTEIRA " +
                     " WHERE u.ID_USUARIO = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    /**
     * DML SELECT: Sobrecarga para busca por ID inteiro.
     *
     * @param id Identificador do usuário
     * @return Instância de Usuario encontrada ou null se inexistente
     * @throws SQLException Em caso de erro na execução SQL
     */
    public Usuario buscarPorId(int id) throws SQLException {
        return buscarPorId((long) id);
    }

    /**
     * DML SELECT: Lista todos os usuários cadastrados no banco de dados com suas Carteiras.
     *
     * @return Lista com todos os usuários mapeados
     * @throws SQLException Em caso de erro na execução SQL
     */
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT u.ID_USUARIO, u.NM_COMPLETO, u.NR_CPF, u.DS_EMAIL, u.DS_SENHA_HASH, " +
                     "       u.DT_NASCIMENTO, u.DS_MODO_INTERFACE, u.VL_LIMITE_MENSAL, " +
                     "       u.FL_KYC_APROVADO, u.ID_CARTEIRA, " +
                     "       c.DS_ENDERECO_DIGITAL, c.VL_SALDO_DISPONIVEL_BRL " +
                     "  FROM TB_USUARIO u " +
                     "  INNER JOIN TB_CARTEIRA c ON u.ID_CARTEIRA = c.ID_CARTEIRA " +
                     " ORDER BY u.ID_USUARIO";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Usuario> usuarios = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            return usuarios;
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }

    /**
     * Método auxiliar para mapear o registro atual do ResultSet para um objeto Usuario.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Long idUsuario = rs.getLong("ID_USUARIO");
        String nomeCompleto = rs.getString("NM_COMPLETO");
        String cpf = rs.getString("NR_CPF");
        String email = rs.getString("DS_EMAIL");
        String senhaHash = rs.getString("DS_SENHA_HASH");
        Date dataNascimentoSql = rs.getDate("DT_NASCIMENTO");
        LocalDate dataNascimento = (dataNascimentoSql != null) ? dataNascimentoSql.toLocalDate() : null;

        Long idCarteira = rs.getLong("ID_CARTEIRA");
        String enderecoDigital = rs.getString("DS_ENDERECO_DIGITAL");
        BigDecimal saldoBrl = rs.getBigDecimal("VL_SALDO_DISPONIVEL_BRL");

        Carteira carteira = new Carteira(idCarteira, enderecoDigital);
        if (saldoBrl != null && saldoBrl.compareTo(BigDecimal.ZERO) > 0) {
            carteira.depositarBrl(saldoBrl);
        }

        Usuario usuario = new Usuario(idUsuario, nomeCompleto, cpf, email, senhaHash, dataNascimento, carteira);

        String modoStr = rs.getString("DS_MODO_INTERFACE");
        if (modoStr != null) {
            try {
                usuario.setModoInterface(ModoInterface.valueOf(modoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                usuario.setModoInterface(ModoInterface.INICIANTE);
            }
        }

        BigDecimal limite = rs.getBigDecimal("VL_LIMITE_MENSAL");
        if (limite != null) {
            usuario.setLimiteOperacionalMensal(limite);
        }

        String kycStr = rs.getString("FL_KYC_APROVADO");
        usuario.setKycAprovado("S".equalsIgnoreCase(kycStr));

        return usuario;
    }
}
