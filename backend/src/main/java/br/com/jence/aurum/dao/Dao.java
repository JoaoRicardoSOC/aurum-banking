package br.com.jence.aurum.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface genérica para a camada de acesso a dados (DAO).
 * Define as operações fundamentais de CRUD (Create, Read, Update, Delete)
 * e listagem para qualquer entidade do domínio.
 *
 * @param <T> Tipo da entidade de domínio
 * @param <K> Tipo da chave primária (ID)
 */
public interface Dao<T, K> {

    /**
     * DML INSERT: Persiste uma nova entidade no banco de dados.
     * Deve preencher a chave primária gerada na instância passada como parâmetro.
     *
     * @param entidade Instância da entidade a ser inserida
     * @throws SQLException Em caso de erro na execução do comando SQL
     */
    void inserir(T entidade) throws SQLException;

    /**
     * DML UPDATE: Atualiza os dados de uma entidade existente no banco de dados.
     *
     * @param entidade Instância com dados atualizados e ID correspondente
     * @throws SQLException Em caso de erro na execução do comando SQL
     */
    void atualizar(T entidade) throws SQLException;

    /**
     * DML DELETE: Remove uma entidade do banco de dados pelo seu identificador único.
     *
     * @param id Chave primária da entidade a ser removida
     * @throws SQLException Em caso de erro na execução do comando SQL
     */
    void excluir(K id) throws SQLException;

    /**
     * DML SELECT: Busca uma entidade pelo seu identificador primário.
     *
     * @param id Chave primária da entidade
     * @return Instância da entidade encontrada ou null caso não exista
     * @throws SQLException Em caso de erro na execução do comando SQL
     */
    T buscarPorId(K id) throws SQLException;

    /**
     * DML SELECT: Retorna todas as instâncias da entidade armazenadas na tabela.
     *
     * @return Lista contendo todas as entidades encontradas (manipulada com Collections)
     * @throws SQLException Em caso de erro na execução do comando SQL
     */
    List<T> listarTodos() throws SQLException;
}
