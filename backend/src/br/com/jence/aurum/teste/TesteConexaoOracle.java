package br.com.jence.aurum.teste;

import br.com.jence.aurum.factory.ConnectionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class TesteConexaoOracle {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  AURUM BANKING - TESTE DE CONEXÃO AO ORACLE FIAP");
        System.out.println("===============================================================");
        System.out.println("Tentando conectar com as credenciais:");
        System.out.println(" -> URL:      jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL");
        System.out.println(" -> Usuário:  rm563609");
        System.out.println("---------------------------------------------------------------");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            long tempoInicio = System.currentTimeMillis();
            conn = ConnectionFactory.getConnection();
            long tempoFim = System.currentTimeMillis();

            System.out.println("\n✅ CONEXÃO ESTABELECIDA COM SUCESSO!");
            System.out.println("   Tempo de resposta: " + (tempoFim - tempoInicio) + " ms\n");

            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println(" Informações do Servidor Oracle:");
            System.out.println("   -> Produto: " + metaData.getDatabaseProductName());
            System.out.println("   -> Versão do Banco: " + metaData.getDatabaseProductVersion());
            System.out.println("   -> Driver Utilizado: " + metaData.getDriverName() + " (v" + metaData.getDriverVersion() + ")");

            stmt = conn.createStatement();

            // Teste de consulta simples no dual
            rs = stmt.executeQuery("SELECT USER, TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') AS DATA_HORA FROM DUAL");
            if (rs.next()) {
                System.out.println("\n Sessão Ativa:");
                System.out.println("   -> Usuário Logado: " + rs.getString("USER"));
                System.out.println("   -> Data/Hora Servidor: " + rs.getString("DATA_HORA"));
            }
            rs.close();

            // Listar tabelas criadas no schema
            System.out.println("\n Tabelas encontradas no Schema do usuário:");
            rs = stmt.executeQuery("SELECT table_name FROM user_tables ORDER BY table_name");
            int totalTabelas = 0;
            while (rs.next()) {
                totalTabelas++;
                System.out.println("   [" + totalTabelas + "] " + rs.getString("TABLE_NAME"));
            }

            if (totalTabelas == 0) {
                System.out.println("   (Nenhuma tabela criada ainda. Execute o script_ddl.sql no Oracle SQL Developer!)");
            } else {
                System.out.println("   Total de tabelas existentes: " + totalTabelas);
            }

            System.out.println("\n===============================================================");
            System.out.println("  TESTE CONCLUÍDO COM 100% DE SUCESSO!");
            System.out.println("===============================================================");

        } catch (Exception e) {
            System.err.println("\n❌ FALHA AO CONECTAR AO BANCO DE DADOS ORACLE:");
            System.err.println("   Mensagem: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(conn, stmt, rs);
        }
    }
}
