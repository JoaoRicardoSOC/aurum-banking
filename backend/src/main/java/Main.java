import br.com.jence.aurum.model.*;
import br.com.jence.aurum.service.TransacaoService;
import br.com.jence.aurum.dao.*;
import br.com.jence.aurum.factory.ConnectionFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("🚀 INICIANDO BATERIA DE TESTES DA PLATAFORMA AURUM");
        System.out.println("=====================================================\n");

        System.out.println("Começando testes unitários:");

        System.out.println("\nTestando Usuário:");
        testarCriacaoUsuarioValido();
        testarCpfInvalido();
        testarUsuarioMenorDeIdade();
        testarKyc();
        testarLimiteSemKyc();

        System.out.println("\nTestando Carteira:");
        testarDepositoCarteira();
        testarSaqueSemSaldo();

        System.out.println("\nTestando Criptoativo:");
        testarCriptoativo();
        testarAtualizacaoPrecoCripto();

        System.out.println("\nTestando PosiçãoCripto:");
        testarCriacaoPosicaoCriptoValida();
        testarPosicaoAdicionar();
        testarPosicaoSubtrair();
        testarPosicaoSaldoNegativo();

        System.out.println("\nTestando Cofre:");
        testarCofreTemporal();
        testarResgateAntecipado();

        System.out.println("\nTestando ItemCombo:");
        testarCriacaoItemComboValido();
        testarItemComboPercentualInvalido();

        System.out.println("\nTestando Combo:");
        testarComboValido();
        testarComboIncompleto();
        testarComboInvalido();

        System.out.println("\nTestando Transação:");
        testarTransacaoConcluida();
        testarFalhaEmTransacaoConcluida();

        System.out.println("\nTestando Empresa:");
        testarCriacaoEmpresaValida();
        testarEmpresaCNPJInvalido();
        testarEmpresaSemUsuarioMaster();
        testarAdicionarGuardiao();
        testarGuardiaoDuplicado();
        testarRegistrarRelatorio();
        testarTransferenciaUsuarioMaster();
        testarTransferenciaMasterEmpresaAtiva();

        System.out.println("\nTestando Solicitação:");
        testarSolicitacaoAprovada();
        testarVotoDuplicado();
        testarSolicitacaoExpirada();

        System.out.println("\nTestando Guardião:");
        testarCriacaoGuardiaoValido();

        System.out.println("\nTestando Notificação:");
        testarNotificacao();

        System.out.println("\nTestando Relatório:");
        testarRelatorioFiscal();

        System.out.println("\nTestando Aula:");
        testarCriacaoAulaValida();
        testarTituloVazio();
        testarConteudoVazio();
        testarOrdemNaoPositiva();
        testarXPNaoPositivo();
        testarRetornoDeXP();

        System.out.println("\nTestando ProgressoUsuarioAula:");
        testarCriacaoProgressoUsuarioAulaValido();
        testarMarcarComoConcluidaEReverter();

        System.out.println("\n======================================================\n");
        System.out.println("Começando Testes de Fluxo");

        testarCompraDeCripto();
        testarVendaDeCripto();
        testarCompraDeComboCriptoativos();
        testarMultiSignatureCorporativo();
        testarGamificacaoEducacional();

        System.out.println("\n======================================================\n");
        System.out.println("Começando Testes de Coleções e Arquivos");

        testarUsoDeColecoesEArquivos();

        System.out.println("\n======================================================\n");
        System.out.println("Começando Testes de Integração com Banco de Dados (JDBC / DAO)");

        testarIntegracaoDatabaseJdbc();

        System.out.println("\n=====================================================");
        System.out.println("          TODOS OS TESTES FORAM CONCLUÍDOS          ");
        System.out.println("=====================================================");
    }

    // TESTES UNITARIOS
    // Usuario

    private static void testarCriacaoUsuarioValido() {
        System.out.println("\n--- testarCriacaoUsuarioValido ---");

        try {
            Carteira carteira = new Carteira(1L, "0xUSER");

            Usuario user = new Usuario(
                    1L,
                    "Carlos Silva",
                    "12345678901",
                    "carlos@email.com",
                    "hash123",
                    LocalDate.of(1990, 5, 10),
                    carteira
            );

            System.out.println("[OK] Usuário criado");

            System.out.println("Nome: " + user.getNomeCompleto());
            System.out.println("KYC: " + user.isKycAprovado());
            System.out.println("Limite: " + user.getLimiteOperacionalMensal());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarCpfInvalido() {
        System.out.println("\n--- testarCpfInvalido ---");

        try {
            Carteira carteira = new Carteira(1L, "0xUSER");

            new Usuario(
                    1L,
                    "Carlos",
                    "123",
                    "email@email.com",
                    "hash",
                    LocalDate.of(1990, 1, 1),
                    carteira
            );

            System.out.println("[FALHA] CPF inválido foi aceito");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] CPF inválido bloqueado");
        }
    }

    private static void testarUsuarioMenorDeIdade() {
        System.out.println("\n--- testarUsuarioMenorDeIdade ---");

        try {
            Carteira carteira = new Carteira(1L, "0xUSER");

            new Usuario(
                    1L,
                    "Menor",
                    "12345678901",
                    "menor@email.com",
                    "hash",
                    LocalDate.now().minusYears(15),
                    carteira
            );

            System.out.println("[FALHA] Usuário menor foi aceito");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Menor de idade bloqueado");
        }
    }

    private static void testarKyc() {
        System.out.println("\n--- testarKyc ---");

        try {
            Usuario user = criarUsuarioBase();

            user.aprovarKyc(new BigDecimal("50000"));

            System.out.println("[OK] KYC aprovado");
            System.out.println("Novo limite: " + user.getLimiteOperacionalMensal());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarLimiteSemKyc() {
        System.out.println("\n--- testarLimiteSemKyc ---");

        try {
            Usuario user = criarUsuarioBase();

            user.ajustarLimiteOperacional(new BigDecimal("10000"));

            System.out.println("[FALHA] Ajustou limite sem KYC");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Ajuste sem KYC bloqueado");
        }
    }

    // Carteira

    private static void testarDepositoCarteira() {
        System.out.println("\n--- testarDepositoCarteira ---");

        try {
            Carteira carteira = new Carteira(1L, "0xABC");

            carteira.depositarBrl(new BigDecimal("1000"));

            System.out.println("[OK] Depósito realizado");
            System.out.println("Saldo: " + carteira.getSaldoDisponivelBrl());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarSaqueSemSaldo() {
        System.out.println("\n--- testarSaqueSemSaldo ---");

        try {
            Carteira carteira = new Carteira(1L, "0xABC");

            carteira.sacarBrl(new BigDecimal("500"));

            System.out.println("[FALHA] Saque sem saldo permitido");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Saque sem saldo bloqueado");
        }
    }

    // Criptoativo

    private static void testarCriptoativo() {
        System.out.println("\n--- testarCriptoativo ---");

        try {
            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "btc",
                    new BigDecimal("350000")
            );

            System.out.println("[OK] Criptoativo criado");
            System.out.println("Sigla: " + btc.getSigla());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarAtualizacaoPrecoCripto() {
        System.out.println("\n--- testarAtualizacaoPrecoCripto ---");

        try {
            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "BTC",
                    new BigDecimal("100")
            );

            btc.atualizarPreco(new BigDecimal("120"));

            System.out.println("[OK] Preço atualizado");
            System.out.println("Novo preço: " + btc.getPrecoAtualBrl());
            System.out.println("Variação: " + btc.getVariacao24h() + "%");

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    // PosicaoCripto

    private static void testarCriacaoPosicaoCriptoValida() {
        System.out.println("\n--- testarCriacaoPosicaoCriptoValida ---");

        try {
            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            PosicaoCripto posicao = new PosicaoCripto(
                    1L,
                    criptoativo,
                    new BigDecimal("0.1")
            );

            System.out.println("[OK] PosiçãoCripto criada com sucesso");
        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarPosicaoAdicionar() {
        System.out.println("\n--- testarPosicaoAdicionar ---");

        try {
            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            PosicaoCripto posicao = new PosicaoCripto(
                    1L,
                    criptoativo,
                    new BigDecimal("0.1")
            );

            BigDecimal valor = new BigDecimal("0.1");

            System.out.println("Quantidade Inicial: " + posicao.getQuantidadeTotal());

            posicao.adicionarQuantidade(valor);

            System.out.println("Quantidade Adicionada: " + valor);
            System.out.println("Quantidade Total: " + posicao.getQuantidadeTotal());

            System.out.println("[OK] Adição realizada com sucesso");

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarPosicaoSubtrair() {
        System.out.println("\n--- testarPosicaoSubtrair ---");

        try {
            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            PosicaoCripto posicao = new PosicaoCripto(
                    1L,
                    criptoativo,
                    new BigDecimal("0.1")
            );

            BigDecimal valor = new BigDecimal("0.05");

            System.out.println("Quantidade Inicial: " + posicao.getQuantidadeTotal());

            posicao.subtrairQuantidade(valor);

            System.out.println("Quantidade Subtraída: " + valor);
            System.out.println("Quantidade Total: " + posicao.getQuantidadeTotal());

            System.out.println("[OK] Subtração realizada com sucesso");

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarPosicaoSaldoNegativo() {
        System.out.println("\n--- testarPosicaoSaldoNegativo ---");

        try {
            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            PosicaoCripto posicao = new PosicaoCripto(
                    1L,
                    criptoativo,
                    new BigDecimal("0.1")
            );

            BigDecimal valor = new BigDecimal("0.2");

            posicao.subtrairQuantidade(valor);

            System.out.println("[FALHA] Saldo negativo passou");

        } catch (IllegalStateException e) {
            System.out.println("[OK] " + e.getMessage());
        }
    }

    // Cofre

    private static void testarCofreTemporal() {
        System.out.println("\n--- testarCofreTemporal ---");

        try {

            Carteira carteira = new Carteira(1L, "0xCOFRE");

            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "BTC",
                    new BigDecimal("300000")
            );

            CofreTemporal cofre = new CofreTemporal(
                    1L,
                    "Hold BTC",
                    LocalDate.now().plusDays(10),
                    new BigDecimal("0.5"),
                    btc,
                    carteira
            );

            System.out.println("[OK] Cofre criado");
            System.out.println("Status: " + cofre.getStatus());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarResgateAntecipado() {
        System.out.println("\n--- testarResgateAntecipado ---");

        try {

            Carteira carteira = new Carteira(1L, "0xCOFRE");

            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "BTC",
                    new BigDecimal("300000")
            );

            CofreTemporal cofre = new CofreTemporal(
                    1L,
                    "Hold BTC",
                    LocalDate.now().plusDays(5),
                    new BigDecimal("1"),
                    btc,
                    carteira
            );

            cofre.processarResgate();

            System.out.println("[FALHA] Resgate antecipado permitido");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Resgate antecipado bloqueado");
        }
    }

    // ItemCombo

    private static void testarCriacaoItemComboValido() {
        System.out.println("\n--- testarCriacaoItemComboValido ---");

        try {
            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            ItemCombo itemCombo = new ItemCombo(
                    1L,
                    criptoativo,
                    new BigDecimal("15")
            );

            System.out.println("[OK] ItemCombo criado com sucesso");

        } catch (Exception e) {
            System.out.println("[FALHOU] - " + e.getMessage());
        }
    }

    private static void testarItemComboPercentualInvalido() {
        System.out.println("\n--- testarItemComboPercentualInvalido ---");

        try {
            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            ItemCombo itemCombo = new ItemCombo(
                    1L,
                    criptoativo,
                    new BigDecimal("110")
            );

            System.out.println("[FALHOU] Valor percentual inválido passou");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] - " + e.getMessage());
        }
    }

    // Combo

    private static void testarComboValido() {
        System.out.println("\n--- testarComboValido ---");

        try {

            ComboCriptoativos combo = new ComboCriptoativos(
                    1L,
                    "Combo DeFi",
                    ComboCriptoativos.PerfilRisco.ALTO
            );

            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "BTC",
                    new BigDecimal("100")
            );

            Criptoativo eth = new Criptoativo(
                    2L,
                    "Ethereum",
                    "ETH",
                    new BigDecimal("50")
            );

            combo.adicionarItem(
                    new ItemCombo(1L, btc, new BigDecimal("60"))
            );

            combo.adicionarItem(
                    new ItemCombo(2L, eth, new BigDecimal("40"))
            );

            combo.setDisponivel(true);

            System.out.println("[OK] Combo ativado");

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarComboIncompleto() {
        System.out.println("\n--- testarComboIncompleto ---");

        try {

            ComboCriptoativos combo = new ComboCriptoativos(
                    1L,
                    "Combo Incompleto",
                    ComboCriptoativos.PerfilRisco.ALTO
            );

            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "BTC",
                    new BigDecimal("100")
            );

            combo.adicionarItem(
                    new ItemCombo(1L, btc, new BigDecimal("70"))
            );

            combo.setDisponivel(true);

            System.out.println("[FALHA] Combo incompleto ativado");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Combo incompleto bloqueado");
        }
    }

    private static void testarComboInvalido() {
        System.out.println("\n--- testarComboInvalido ---");

        try {

            ComboCriptoativos combo = new ComboCriptoativos(
                    1L,
                    "Combo Incompleto",
                    ComboCriptoativos.PerfilRisco.ALTO
            );

            Criptoativo btc = new Criptoativo(
                    1L,
                    "Bitcoin",
                    "BTC",
                    new BigDecimal("100")
            );

            Criptoativo eth = new Criptoativo(
                    2L,
                    "Ethereum",
                    "ETH",
                    new BigDecimal("50")
            );

            combo.adicionarItem(
                    new ItemCombo(1L, btc, new BigDecimal("70"))
            );

            combo.adicionarItem(
                    new ItemCombo(1L, eth, new BigDecimal("40"))
            );

            combo.setDisponivel(true);

            System.out.println("[FALHA] Combo inválido ativado");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Combo inválido bloqueado");
        }
    }

    // Transacao

    private static void testarTransacaoConcluida() {
        System.out.println("\n--- testarTransacaoConcluida ---");

        try {

            Transacao t = criarTransacaoBase();

            t.marcarComoConcluida();

            System.out.println("[OK] Transação concluída");
            System.out.println("Status: " + t.getStatus());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarFalhaEmTransacaoConcluida() {
        System.out.println("\n--- testarFalhaEmTransacaoConcluida ---");

        try {

            Transacao t = criarTransacaoBase();

            t.marcarComoConcluida();

            t.registrarFalha();

            System.out.println("[FALHA] Falha permitida em transação concluída");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Falha em transação concluída bloqueada");
        }
    }

    // Empresa

    private static void testarCriacaoEmpresaValida() {
        System.out.println("\n--- testarCriacaoEmpresaValida ---");

        try {

            Usuario user = criarUsuarioBase();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user,
                    carteiraEmpresa
            );

            System.out.println("[OK] Empresa criada");

            System.out.println("Razão Social: " + empresa.getRazaoSocial());
            System.out.println("Empresa está ativa: " + empresa.isStatusAtivo());
            System.out.println("Usuário Master: " + empresa.getUsuarioMaster());
            System.out.println("Data de Cadastro: " + empresa.getDataCadastro());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarEmpresaCNPJInvalido() {
        System.out.println("\n--- testarEmpresaCNPJInvalido ---");

        try {

            Usuario user = criarUsuarioBase();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "1234567890123d",
                    user,
                    carteiraEmpresa
            );

            System.out.println("[FALHA] CNPJ inválido foi aceito");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] CNPJ inválido bloqueado");
        }
    }

    private static void testarEmpresaSemUsuarioMaster() {
        System.out.println("\n--- testarEmpresaSemUsuarioMaster ---");

        try {

            Usuario user = null;

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "1234567890123d",
                    user,
                    carteiraEmpresa
            );

            System.out.println("[FALHA] Empresa sem usuário master foi aceito");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Empresa sem usuário master bloqueado");
        }
    }

    private static void testarAdicionarGuardiao() {
        System.out.println("\n--- testarAdicionarGuardiao ---");

        try {

            Usuario user1 = criarUsuarioBase();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user1,
                    carteiraEmpresa
            );

            Guardiao g1 = new Guardiao(1L, user1, empresa);
            Guardiao g2 = new Guardiao(2L, criarUsuarioBase2(), empresa);

            empresa.adicionarGuardiao(g1);

            System.out.println("[OK] Primeiro Guardião Adicionado: " + empresa.getConselhoGuardioes());

            empresa.adicionarGuardiao(g2);

            System.out.println("[OK] Segundo Guardião Adicionado: " + empresa.getConselhoGuardioes());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarGuardiaoDuplicado() {
        System.out.println("\n--- testarGuardiaoDuplicado ---");

        try {

            Usuario user1 = criarUsuarioBase();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user1,
                    carteiraEmpresa
            );

            Guardiao g1 = new Guardiao(1L, user1, empresa);

            empresa.adicionarGuardiao(g1);
            empresa.adicionarGuardiao(g1);

            System.out.println("[FALHA] Guardião Duplicado foi aceito: " + empresa.getConselhoGuardioes());

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Guardião Duplicado foi bloqueado");
        }
    }

    private static void testarRegistrarRelatorio() {
        System.out.println("\n--- testarRegistrarRelatorio ---");

        try {

            Usuario user = criarUsuarioBase();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user,
                    carteiraEmpresa
            );

            RelatorioFiscal relatorioFiscal = new RelatorioFiscal(
                    1L,
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    RelatorioFiscal.FormatoArquivo.PDF,
                    empresa
            );

            empresa.registrarRelatorio(relatorioFiscal);

            System.out.println("[OK] Relatório Fiscal registrado: " + empresa.getHistoricoRelatorios());

        } catch (Exception e) {
            System.out.println("[FALHA] Relatório Fiscal não registrado - " + e.getMessage());
        }
    }

    public static void testarTransferenciaUsuarioMaster() {
        System.out.println("\n--- testarTransferenciaUsuarioMaster ---");

        try {
            Usuario user1 = criarUsuarioBase();

            Usuario user2 = criarUsuarioBase2();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user1,
                    carteiraEmpresa
            );

            empresa.suspenderEmpresa();

            empresa.transferirMaster(user2);

            System.out.println("[OK] Usuário Master Transferido com a Empresa Suspensa");

            empresa.setStatusAtivo(true);
        } catch (IllegalStateException e) {
            System.out.println("[FALHA] Usuário Master não foi Transferido - " + e.getMessage());
        }
    }

    public static void testarTransferenciaMasterEmpresaAtiva() {
        System.out.println("\n--- testarTransferenciaMasterEmpresaAtiva ---");

        try {
            Usuario user1 = criarUsuarioBase();

            Usuario user2 = criarUsuarioBase2();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user1,
                    carteiraEmpresa
            );

            empresa.transferirMaster(user2);

            System.out.println("[FALHA] Usuário Master foi transferido com a empresa ativa");

        } catch (IllegalStateException e) {
            System.out.println("[OK] Usuário Master não foi transferido com a empresa ativa - " + e.getMessage());
        }
    }

    // Solicitacao

    private static void testarSolicitacaoAprovada() {
        System.out.println("\n--- testarSolicitacaoAprovada ---");

        try {

            Usuario master = criarUsuarioBase();

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Corp",
                    "12345678000199",
                    master,
                    master.getCarteira()
            );

            Guardiao g1 = new Guardiao(1L, criarUsuarioBase(), empresa);
            Guardiao g2 = new Guardiao(2L, criarUsuarioBase2(), empresa);

            SolicitacaoDeTransacao solicitacao =
                    new SolicitacaoDeTransacao(
                            1L,
                            criarTransacaoBase(),
                            2,
                            LocalDateTime.now().plusDays(1)
                    );

            g1.votar(solicitacao, true, "");
            g2.votar(solicitacao, true, "");

            System.out.println("[OK] Solicitação aprovada");
            System.out.println("Status: " + solicitacao.getStatus());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarVotoDuplicado() {
        System.out.println("\n--- testarVotoDuplicado ---");

        try {

            Usuario master = criarUsuarioBase();

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Corp",
                    "12345678000199",
                    master,
                    master.getCarteira()
            );

            Guardiao g1 = new Guardiao(1L, criarUsuarioBase(), empresa);

            SolicitacaoDeTransacao solicitacao =
                    new SolicitacaoDeTransacao(
                            1L,
                            criarTransacaoBase(),
                            1,
                            LocalDateTime.now().plusDays(1)
                    );

            g1.votar(solicitacao, true, "");
            g1.votar(solicitacao, true, "");

            System.out.println("[FALHA] Voto duplicado permitido");

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("[OK] Voto duplicado bloqueado");
        }
    }

    private static void testarSolicitacaoExpirada() {
        System.out.println("\n--- testarSolicitaocaoExpirada ---");

        try {
            Usuario master = criarUsuarioBase();

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Corp",
                    "12345678000199",
                    master,
                    master.getCarteira()
            );

            Guardiao g1 = new Guardiao(1L, criarUsuarioBase(), empresa);

            SolicitacaoDeTransacao solicitacao = new SolicitacaoDeTransacao(
                    1L,
                    criarTransacaoBase(),
                    1,
                    LocalDateTime.now().minusDays(1)
            );

            g1.votar(solicitacao, true, "");

            System.out.println("[FALHA] Solicitação expirada passou");

        } catch (IllegalStateException e) {
            System.out.println("[OK] - " + e.getMessage());
        }
    }

    // Guardiao
    private static void testarCriacaoGuardiaoValido() {
        System.out.println("\n--- testarCriacaoGuardiaoValido ---");

        try {

            Usuario user = criarUsuarioBase();

            Carteira carteiraEmpresa = new Carteira(
                    1L,
                    "0xEMPRESA"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Banking",
                    "12345678901234",
                    user,
                    carteiraEmpresa
            );

            Guardiao guardiao = new Guardiao(
                    1L,
                    user,
                    empresa
            );

            System.out.println("[OK] Guardião criado");

            System.out.println("Usuário Responsavel: " + guardiao.getUsuarioResponsavel());
            System.out.println("Empresa Protegida: " + guardiao.getEmpresaProtegida());
            System.out.println("Data de Nomeação: " + guardiao.getDataNomeacao());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    // Notificacao

    private static void testarNotificacao() {
        System.out.println("\n--- testarNotificacao ---");

        try {

            Usuario user = criarUsuarioBase();

            Notificacao notificacao = new Notificacao(
                    1L,
                    "Alerta",
                    "Bitcoin caiu",
                    Notificacao.TipoAlerta.AVISO,
                    user
            );

            notificacao.marcarComoLida();

            System.out.println("[OK] Notificação marcada como lida");
            System.out.println("Lida: " + notificacao.isLida());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    // Relatorio

    private static void testarRelatorioFiscal() {
        System.out.println("\n--- testarRelatorioFiscal ---");

        try {

            Usuario user = criarUsuarioBase();

            RelatorioFiscal relatorio =
                    new RelatorioFiscal(
                            1L,
                            LocalDate.of(2025, 1, 1),
                            LocalDate.of(2025, 12, 31),
                            RelatorioFiscal.FormatoArquivo.PDF,
                            user
                    );

            relatorio.finalizarProcessamento(
                    new BigDecimal("1000"),
                    new BigDecimal("2000"),
                    new BigDecimal("1000"),
                    BigDecimal.ZERO,
                    new BigDecimal("5000"),
                    "https://aws.com/arquivo.pdf"
            );

            System.out.println("[OK] Relatório processado");
            System.out.println("Status: " + relatorio.getStatus());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    // Aula

    private static void testarCriacaoAulaValida() {
        System.out.println("\n--- testarCriacaoAulaValida ---");

        try {
            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "Conteúdo da aula",
                    3,
                    10
            );

            System.out.println("[OK] Aula criada com sucesso");

            System.out.println(aula.getTitulo());
            System.out.println(aula.getConteudoHtml());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarTituloVazio() {
        System.out.println("\n--- testarTituloVazio ---");

        try {
            Aula aula = new Aula(
                    1L,
                    "",
                    "Conteúdo da aula",
                    3,
                    10
            );

            System.out.println("[FALHA] Aula criada com título vazio");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] " + e.getMessage());
        }
    }

    private static void testarConteudoVazio() {
        System.out.println("\n--- testarConteudoVazio ---");

        try {
            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "",
                    3,
                    10
            );

            System.out.println("[FALHA] Aula criada com conteúdo vazio");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] " + e.getMessage());
        }
    }

    private static void testarOrdemNaoPositiva() {
        System.out.println("\n--- testarOrdemNaoPositiva ---");

        try {
            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "Conteúdo da aula",
                    0,
                    10
            );

            System.out.println("[FALHA] Aula criada com ordem não positiva");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] " + e.getMessage());
        }
    }

    private static void testarXPNaoPositivo() {
        System.out.println("\n--- testarXPNaoPositivo ---");

        try {
            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "Conteúdo da aula",
                    3,
                    0
            );

            System.out.println("[FALHA] Aula criada com pontos de XP não positivo");

        } catch (IllegalArgumentException e) {
            System.out.println("[OK] " + e.getMessage());
        }
    }

    private static void testarRetornoDeXP() {
        System.out.println("\n--- testarRetornoDeXP ---");

        Aula aula = new Aula(
                1L,
                "Como investir em criptoativos",
                "Conteúdo da aula",
                3,
                10
        );

        System.out.println("Pontos esperados: " + aula.getPontosXp());
        System.out.println("Pontos recebidos: " + aula.finalizarAula());
    }

    // ProgressoUsuarioAula

    private static void testarCriacaoProgressoUsuarioAulaValido() {
        System.out.println("\n--- testarCriacaoProgressoUsuarioAulaValido ---");

        try {
            Usuario usuario = criarUsuarioBase();

            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "Conteúdo da aula",
                    3,
                    10
            );

            ProgressoUsuarioAula progresso = new ProgressoUsuarioAula(
                    1L,
                    usuario,
                    aula
            );

            System.out.println("[OK] ProgressoUsuarioAula criado com sucesso");


            System.out.println("Aluno: " + progresso.getAluno());
            System.out.println("Aula Assistida: " + progresso.getAulaAssistida());
            System.out.println("Concluída: " + progresso.isConcluida());
            System.out.println("Data de Conclusão: " + progresso.getDataConclusao());

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }

    private static void testarMarcarComoConcluidaEReverter() {
        System.out.println("\n--- testarMarcarComoConcluidaEReverter ---");

        try {
            Usuario usuario = criarUsuarioBase();

            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "Conteúdo da aula",
                    3,
                    10
            );

            ProgressoUsuarioAula progresso = new ProgressoUsuarioAula(
                    1L,
                    usuario,
                    aula
            );

            progresso.marcarComoConcluida();

            System.out.println("Concluída: " + progresso.isConcluida());
            System.out.println("Data de Conclusão: " + progresso.getDataConclusao());

            progresso.reverterConclusao();

            System.out.println("Concluída: " + progresso.isConcluida());
            System.out.println("Data de Conclusão: " + progresso.getDataConclusao());

            System.out.println("[OK] Progresso marcado como concluído e revertido");

        } catch (Exception e) {
            System.out.println("[FALHA] " + e.getMessage());
        }
    }


    // AUXILIARES

    private static Usuario criarUsuarioBase() {

        Carteira carteira = new Carteira(
                1L,
                "0xBASE"
        );

        return new Usuario(
                1L,
                "Usuário Base",
                "12345678901",
                "base@email.com",
                "hash",
                LocalDate.of(1990, 1, 1),
                carteira
        );
    }

    private static Usuario criarUsuarioBase2() {

        Carteira carteira = new Carteira(
                2L,
                "0xBASE2"
        );

        return new Usuario(
                2L,
                "Usuário Base 2",
                "98765432100",
                "base2@email.com",
                "hash",
                LocalDate.of(1992, 1, 1),
                carteira
        );
    }

    private static Transacao criarTransacaoBase() {

        Carteira carteira = new Carteira(
                1L,
                "0xTRANS"
        );

        Criptoativo btc = new Criptoativo(
                1L,
                "Bitcoin",
                "BTC",
                new BigDecimal("300000")
        );

        return new Transacao(
                1L,
                Transacao.TipoTransacao.COMPRA,
                new BigDecimal("1000"),
                new BigDecimal("0.01"),
                new BigDecimal("300000"),
                new BigDecimal("10"),
                carteira,
                btc
        );
    }


    // TESTES DE FLUXO

    private static void testarCompraDeCripto() {
        System.out.println("\n--- testarCompraDeCripto ---");

        try {
            Usuario usuario = criarUsuarioBase();

            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            usuario.aprovarKyc(new BigDecimal("7500"));

            usuario.getCarteira().depositarBrl(new BigDecimal("1000"));

            TransacaoService service = new TransacaoService();

            service.realizarCompra(usuario.getCarteira(), criptoativo, new BigDecimal("500"));

            System.out.println("[OK] - Compra concluída com sucesso.");
            System.out.println("Posição Cripto: " + usuario.getCarteira().getAtivosAdquiridos());

        } catch (Exception e) {
            System.out.println("[FALHA] - " + e.getMessage());
        }
    }

    private static void testarVendaDeCripto() {
        System.out.println("\n--- testarVendaDeCripto ---");

        try {
            Usuario usuario = criarUsuarioBase();

            Criptoativo criptoativo = new Criptoativo(
                    1L,
                    "Aurum Coin",
                    "AUC",
                    new BigDecimal("10000")
            );

            PosicaoCripto posicaoCripto = new PosicaoCripto(
                    1L,
                    criptoativo,
                    new BigDecimal("0.1")
            );

            List<PosicaoCripto> ativosAdiquiridos = new ArrayList<>();
            ativosAdiquiridos.add(posicaoCripto);

            usuario.getCarteira().setAtivosAdquiridos(ativosAdiquiridos);

            TransacaoService service = new TransacaoService();

            service.realizarVenda(usuario.getCarteira(), criptoativo, new BigDecimal("500"));

            System.out.println("[OK] - Venda concluída com sucesso.");
            System.out.println("Saldo disponível: " + usuario.getCarteira().getSaldoDisponivelBrl());

        } catch (Exception e) {
            System.out.println("[FALHA] - " + e.getMessage());
        }
    }

    private static void testarCompraDeComboCriptoativos() {
        System.out.println("\n--- testarCompraDeComboCriptoativos ---");

        try {
            Usuario usuario = criarUsuarioBase();

            ItemCombo itemCombo1 = new ItemCombo(
                    1L,
                    new Criptoativo(1L, "Bitcoin", "BTC", new BigDecimal("100000")),
                    new BigDecimal("70")
            );

            ItemCombo itemCombo2 = new ItemCombo(
                    2L,
                    new Criptoativo(2L, "Ethereum", "ETH", new BigDecimal("50000")),
                    new BigDecimal("30")
            );

            ComboCriptoativos comboCriptoativos = new ComboCriptoativos(
                    1L,
                    "Combo Teste",
                    ComboCriptoativos.PerfilRisco.ALTO
            );

            comboCriptoativos.adicionarItem(itemCombo1);

            comboCriptoativos.adicionarItem(itemCombo2);

            usuario.getCarteira().depositarBrl(new BigDecimal("1000"));

            TransacaoService service = new TransacaoService();

            service.realizarCompraCombo(usuario.getCarteira(), comboCriptoativos, new BigDecimal("1000"));

            System.out.println("[OK] - Compra de Combo concluída com sucesso.");
            System.out.println("Posição Cripto: " + usuario.getCarteira().getAtivosAdquiridos());

        } catch (Exception e) {
            System.out.println("[FALHA] - " + e.getMessage());
        }
    }

    private static void testarMultiSignatureCorporativo() {
        System.out.println("\n--- testarMultiSignatureCorporativo ---");

        try {
            TransacaoService transacaoService = new TransacaoService();

            Usuario usuarioMaster = criarUsuarioBase();

            Carteira carteira = new Carteira(
                    1L,
                    "0x1234"
            );

            Empresa empresa = new Empresa(
                    1L,
                    "Aurum Bank",
                    "12345678901234",
                    usuarioMaster,
                    carteira
            );

            carteira.depositarBrl(new BigDecimal("1000"));

            Guardiao g1 = new Guardiao(1L, usuarioMaster, empresa);
            Guardiao g2 = new Guardiao(1L, criarUsuarioBase2(), empresa);

            Criptoativo criptoativo = new Criptoativo(1L, "Aurum Coin", "AUC", new BigDecimal("10000"));

            SolicitacaoDeTransacao solicitacao = new SolicitacaoDeTransacao(
                    1L,
                    criarTransacaoBase(),
                    2,
                    LocalDateTime.now().plusDays(1)
            );

            solicitacao.registrarVoto(g1, true, "");
            solicitacao.registrarVoto(g2, true, "");

            if (solicitacao.getStatus() == SolicitacaoDeTransacao.StatusSolicitacao.APROVADA) {
                transacaoService.realizarCompra(
                        empresa.getCarteira(),
                        solicitacao.getTransacaoPendente().getMoedaEnvolvida(),
                        solicitacao.getTransacaoPendente().getValorMovimentadoBrl()
                );
            }

            System.out.println("[OK] - Solicitação de compra concluída com sucesso.");
            System.out.println("Posição Cripto: " + empresa.getCarteira().getAtivosAdquiridos());

        } catch (Exception e) {
            System.out.println("[FALHA] - " + e.getMessage());
        }
    }

    private static void testarGamificacaoEducacional() {
        System.out.println("\n--- testarGamificacaoEducacional ---");

        try {
            Usuario usuario = criarUsuarioBase();

            Aula aula = new Aula(
                    1L,
                    "Como investir em criptoativos",
                    "conteudo da aula",
                    1,
                    10
            );

            ProgressoUsuarioAula progresso = new ProgressoUsuarioAula(
                    1L,
                    usuario,
                    aula
            );

            aula.iniciarAula();

            aula.finalizarAula();
            progresso.marcarComoConcluida();

            System.out.println("[OK] - Aula concluída com sucesso.");
            System.out.println("Data de Conclusão: " + progresso.getDataConclusao());

        } catch (Exception e) {
            System.out.println("[FALHA] - " + e.getMessage());
        }
    }

    // COLEÇÕES E ARQUIVOS

    private static void testarUsoDeColecoesEArquivos() {
        System.out.println("\n--- testarUsoDeColecoesEArquivos ---");

        try {
            // Testando o uso de ArrayList com as classes Usuario e Criptoativo
            List<Usuario> listaUsuarios = new ArrayList<>();
            listaUsuarios.add(criarUsuarioBase());
            listaUsuarios.add(criarUsuarioBase2());

            List<Criptoativo> listaCriptos = new ArrayList<>();
            listaCriptos.add(new Criptoativo(1L, "Bitcoin", "BTC", new BigDecimal("350000")));
            listaCriptos.add(new Criptoativo(2L, "Ethereum", "ETH", new BigDecimal("15000")));

            // Testando o uso de HashMap com as classes Usuario e Criptoativo
            Map<String, Usuario> mapaUsuarios = new HashMap<>();
            for (Usuario u : listaUsuarios) {
                mapaUsuarios.put(u.getCpf(), u);
            }

            Map<String, Criptoativo> mapaCriptos = new HashMap<>();
            for (Criptoativo c : listaCriptos) {
                mapaCriptos.put(c.getSigla(), c);
            }

            // Exportando os dados das colecoes para um arquivo de texto
            String caminhoArquivo = "dados_exportados.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
                writer.write("--- Relatorio de Usuarios ---\n");
                for (Usuario u : mapaUsuarios.values()) {
                    writer.write("ID: " + u.getId() + ", Nome: " + u.getNomeCompleto() + ", CPF: " + u.getCpf() + ", E-mail: " + u.getEmail() + "\n");
                }
                
                writer.write("\n--- Relatorio de Criptoativos ---\n");
                for (Criptoativo c : mapaCriptos.values()) {
                    writer.write("ID: " + c.getId() + ", Nome: " + c.getNome() + ", Sigla: " + c.getSigla() + ", Preco Atual: R$ " + c.getPrecoAtualBrl() + "\n");
                }
                System.out.println("[OK] Dados exportados com sucesso para o arquivo: " + caminhoArquivo);
            } catch (IOException e) {
                System.out.println("[FALHA] Erro ao escrever no arquivo: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("[FALHA] - " + e.getMessage());
        }
    }

    // =========================================================================
    // TESTES DE INTEGRAÇÃO JDBC / DAO (FASE 6 - CRUD COMPLETO EM TODAS AS 11 TABELAS)
    // =========================================================================

    private static void testarIntegracaoDatabaseJdbc() {
        System.out.println("\n===============================================================================");
        System.out.println("  🏛️  INICIANDO SUÍTE DE INTEGRAÇÃO JDBC / DAO COM BANCO DE DADOS ORACLE FIAP");
        System.out.println("===============================================================================");

        CarteiraDao carteiraDao = new CarteiraDao();
        UsuarioDao usuarioDao = new UsuarioDao();
        EmpresaDao empresaDao = new EmpresaDao();
        CriptoativoDao criptoativoDao = new CriptoativoDao();
        PosicaoCriptoDao posicaoCriptoDao = new PosicaoCriptoDao();
        TransacaoDao transacaoDao = new TransacaoDao();
        CofreTemporalDao cofreTemporalDao = new CofreTemporalDao();
        GuardiaoDao guardiaoDao = new GuardiaoDao();
        SolicitacaoTransacaoDao solicitacaoDao = new SolicitacaoTransacaoDao();
        AulaDao aulaDao = new AulaDao();
        ProgressoUsuarioAulaDao progressoDao = new ProgressoUsuarioAulaDao();

        // Referências das entidades que serão criadas para o ciclo de vida e teardown
        Carteira carteiraUser = null;
        Carteira carteiraCorp = null;
        Carteira carteiraGuardiao = null;
        Usuario usuarioMaster = null;
        Usuario usuarioGuardiao = null;
        Empresa empresa = null;
        Criptoativo cripto = null;
        PosicaoCripto posicao = null;
        Transacao transacao = null;
        CofreTemporal cofre = null;
        Guardiao guardiao = null;
        SolicitacaoDeTransacao solicitacao = null;
        Aula aula = null;
        ProgressoUsuarioAula progresso = null;

        try {
            long sufixo = System.currentTimeMillis();

            // -----------------------------------------------------------------
            // 1. TB_CARTEIRA (CarteiraDao)
            // -----------------------------------------------------------------
            System.out.println("\n[1/11] 💳 Testando TB_CARTEIRA via CarteiraDao...");
            carteiraUser = new Carteira(0L, "0xUSER_" + sufixo);
            carteiraUser.depositarBrl(new BigDecimal("15000.00"));
            carteiraDao.inserir(carteiraUser);
            System.out.println("   [INSERT] Carteira criada com sucesso! ID: " + carteiraUser.getId());

            Carteira cConsultada = carteiraDao.buscarPorId(carteiraUser.getId());
            System.out.println("   [SELECT] Carteira recuperada: Endereço = " + cConsultada.getEnderecoDigital() + " | Saldo = R$ " + cConsultada.getSaldoDisponivelBrl());

            cConsultada.depositarBrl(new BigDecimal("5000.00"));
            carteiraDao.atualizar(cConsultada);
            Carteira cAtualizada = carteiraDao.buscarPorId(carteiraUser.getId());
            System.out.println("   [UPDATE] Saldo atualizado com sucesso! Novo Saldo = R$ " + cAtualizada.getSaldoDisponivelBrl());

            List<Carteira> carteiras = carteiraDao.listarTodos();
            System.out.println("   [COLLECTION] Total de carteiras na base: " + carteiras.size() + " (ArrayList)");

            // -----------------------------------------------------------------
            // 2. TB_USUARIO (UsuarioDao)
            // -----------------------------------------------------------------
            System.out.println("\n[2/11] 👤 Testando TB_USUARIO via UsuarioDao...");
            String cpfUser = String.format("%011d", (long) (Math.abs(System.nanoTime() % 100000000000L)));
            usuarioMaster = new Usuario(
                    0L,
                    "Eduardo Silva Teste",
                    cpfUser,
                    "eduardo." + sufixo + "@aurumbank.com",
                    "$2a$12$e8Yh9Zf5G7k1V2x.hashSeguroSimulado1234567890",
                    LocalDate.of(1995, 8, 20),
                    carteiraUser
            );
            usuarioMaster.setModoInterface(Usuario.ModoInterface.INICIANTE);
            usuarioMaster.setLimiteOperacionalMensal(new BigDecimal("4500.00"));
            usuarioMaster.setKycAprovado(false);
            usuarioDao.inserir(usuarioMaster);
            System.out.println("   [INSERT] Usuário criado com sucesso! ID: " + usuarioMaster.getId());

            Usuario uConsultado = usuarioDao.buscarPorId(usuarioMaster.getId());
            System.out.println("   [SELECT] Usuário recuperado: Nome = " + uConsultado.getNomeCompleto() + " | CPF = " + uConsultado.getCpf());

            uConsultado.setNomeCompleto("Eduardo Silva Teste Atualizado");
            uConsultado.aprovarKyc(new BigDecimal("25000.00"));
            usuarioDao.atualizar(uConsultado);
            Usuario uAtualizado = usuarioDao.buscarPorId(usuarioMaster.getId());
            System.out.println("   [UPDATE] Usuário atualizado! Nome = " + uAtualizado.getNomeCompleto() + " | Limite = R$ " + uAtualizado.getLimiteOperacionalMensal());

            List<Usuario> usuarios = usuarioDao.listarTodos();
            System.out.println("   [COLLECTION] Total de usuários na base: " + usuarios.size() + " (ArrayList)");

            // -----------------------------------------------------------------
            // 3. TB_EMPRESA (EmpresaDao)
            // -----------------------------------------------------------------
            System.out.println("\n[3/11] 🏢 Testando TB_EMPRESA via EmpresaDao...");
            carteiraCorp = new Carteira(0L, "0xCORP_" + sufixo);
            carteiraCorp.depositarBrl(new BigDecimal("100000.00"));
            carteiraDao.inserir(carteiraCorp);

            String cnpjCorp = String.format("%014d", (long) (Math.abs(System.nanoTime() % 100000000000000L)));
            empresa = new Empresa(
                    0L,
                    "Aurum Capital Gestora Ltda",
                    cnpjCorp,
                    "diretoria." + sufixo + "@aurumcapital.com.br",
                    usuarioMaster,
                    carteiraCorp
            );
            empresaDao.inserir(empresa);
            System.out.println("   [INSERT] Empresa criada com sucesso! ID: " + empresa.getId());

            Empresa empConsultada = empresaDao.buscarPorId(empresa.getId());
            System.out.println("   [SELECT] Empresa recuperada: Razão Social = " + empConsultada.getRazaoSocial() + " | CNPJ = " + empConsultada.getCnpj());

            empConsultada.setRazaoSocial("Aurum Capital Holding S.A.");
            empresaDao.atualizar(empConsultada);
            Empresa empAtualizada = empresaDao.buscarPorId(empresa.getId());
            System.out.println("   [UPDATE] Empresa atualizada: Nova Razão = " + empAtualizada.getRazaoSocial());

            List<Empresa> empresas = empresaDao.listarTodos();
            System.out.println("   [COLLECTION] Total de empresas cadastradas: " + empresas.size() + " (ArrayList)");

            // -----------------------------------------------------------------
            // 4. TB_CRIPTOATIVO (CriptoativoDao)
            // -----------------------------------------------------------------
            System.out.println("\n[4/11] 🪙 Testando TB_CRIPTOATIVO via CriptoativoDao...");
            String siglaCripto = "AU" + (int)(Math.random() * 900 + 100);
            cripto = new Criptoativo(0L, "Aurum Coin Teste", siglaCripto, new BigDecimal("120.5000"));
            cripto.setLogoUrl("https://aurum.com/assets/" + siglaCripto.toLowerCase() + ".png");
            criptoativoDao.inserir(cripto);
            System.out.println("   [INSERT] Criptoativo inserido! ID: " + cripto.getId() + " | Sigla: " + cripto.getSigla());

            Criptoativo crConsultado = criptoativoDao.buscarPorSigla(siglaCripto);
            System.out.println("   [SELECT] Criptoativo consultado: " + crConsultado.getNome() + " | Cotação = R$ " + crConsultado.getPrecoAtualBrl());

            crConsultado.atualizarPreco(new BigDecimal("135.8000"));
            criptoativoDao.atualizar(crConsultado);
            Criptoativo crAtualizado = criptoativoDao.buscarPorId(cripto.getId());
            System.out.println("   [UPDATE] Preço atualizado! Nova cotação = R$ " + crAtualizado.getPrecoAtualBrl() + " | Variação 24h = " + crAtualizado.getVariacao24h() + "%");

            List<Criptoativo> catalogo = criptoativoDao.listarTodos();
            System.out.println("   [COLLECTION] Catálogo de criptoativos: " + catalogo.size() + " itens (ArrayList)");

            // -----------------------------------------------------------------
            // 5. TB_POSICAO_CRIPTO (PosicaoCriptoDao)
            // -----------------------------------------------------------------
            System.out.println("\n[5/11] 📊 Testando TB_POSICAO_CRIPTO via PosicaoCriptoDao...");
            posicao = new PosicaoCripto(0L, carteiraUser.getId(), cripto, new BigDecimal("2.50000000"));
            posicaoCriptoDao.inserir(posicao);
            System.out.println("   [INSERT] Posição cripto criada! ID: " + posicao.getId());

            PosicaoCripto posConsultada = posicaoCriptoDao.buscarPorId(posicao.getId());
            System.out.println("   [SELECT] Posição recuperada: Quantidade = " + posConsultada.getQuantidadeTotal() + " " + posConsultada.getMoeda().getSigla());

            posConsultada.adicionarQuantidade(new BigDecimal("1.25000000"));
            posicaoCriptoDao.atualizar(posConsultada);
            PosicaoCripto posAtualizada = posicaoCriptoDao.buscarPorId(posicao.getId());
            System.out.println("   [UPDATE] Quantidade acumulada atualizada: " + posAtualizada.getQuantidadeTotal());

            List<PosicaoCripto> posicoes = posicaoCriptoDao.listarPorCarteira(carteiraUser.getId());
            System.out.println("   [COLLECTION] Posições da carteira ID " + carteiraUser.getId() + ": " + posicoes.size() + " (ArrayList)");

            // -----------------------------------------------------------------
            // 6. TB_TRANSACAO (TransacaoDao)
            // -----------------------------------------------------------------
            System.out.println("\n[6/11] 📑 Testando TB_TRANSACAO via TransacaoDao...");
            transacao = new Transacao(
                    0L,
                    Transacao.TipoTransacao.COMPRA,
                    new BigDecimal("339.50"),
                    new BigDecimal("2.50000000"),
                    new BigDecimal("135.8000"),
                    new BigDecimal("2.00"),
                    carteiraUser,
                    cripto
            );
            transacaoDao.inserir(transacao);
            System.out.println("   [INSERT] Transação registrada no livro-razão! ID: " + transacao.getId());

            Transacao trConsultada = transacaoDao.buscarPorId(transacao.getId());
            System.out.println("   [SELECT] Transação recuperada: Tipo = " + trConsultada.getTipoTransacao() + " | Status = " + trConsultada.getStatus() + " | Valor = R$ " + trConsultada.getValorMovimentadoBrl());

            trConsultada.marcarComoConcluida();
            transacaoDao.atualizar(trConsultada);
            Transacao trAtualizada = transacaoDao.buscarPorId(transacao.getId());
            System.out.println("   [UPDATE] Transação concluída com sucesso! Novo status = " + trAtualizada.getStatus());

            List<Transacao> extrato = transacaoDao.listarPorCarteira(carteiraUser.getId());
            System.out.println("   [COLLECTION] Extrato da carteira: " + extrato.size() + " transações (ArrayList)");

            // -----------------------------------------------------------------
            // 7. TB_COFRE_TEMPORAL (CofreTemporalDao)
            // -----------------------------------------------------------------
            System.out.println("\n[7/11] 🔒 Testando TB_COFRE_TEMPORAL via CofreTemporalDao...");
            cofre = new CofreTemporal(
                    0L,
                    "Reserva Estratégica 2027",
                    LocalDate.now().plusDays(60),
                    new BigDecimal("1.00000000"),
                    cripto,
                    carteiraUser
            );
            cofreTemporalDao.inserir(cofre);
            System.out.println("   [INSERT] Cofre temporal criado! ID: " + cofre.getId());

            CofreTemporal cofConsultado = cofreTemporalDao.buscarPorId(cofre.getId());
            System.out.println("   [SELECT] Cofre recuperado: Objetivo = " + cofConsultado.getNome() + " | Liberação = " + cofConsultado.getDataLiberacao() + " | Status = " + cofConsultado.getStatus());

            cofConsultado.setStatus(CofreTemporal.StatusCofre.CANCELADO);
            cofreTemporalDao.atualizar(cofConsultado);
            CofreTemporal cofAtualizado = cofreTemporalDao.buscarPorId(cofre.getId());
            System.out.println("   [UPDATE] Cofre atualizado! Novo status = " + cofAtualizado.getStatus());

            List<CofreTemporal> cofres = cofreTemporalDao.listarTodos();
            System.out.println("   [COLLECTION] Total de cofres temporais ativos: " + cofres.size() + " (ArrayList)");

            // -----------------------------------------------------------------
            // 8. TB_GUARDIAO (GuardiaoDao)
            // -----------------------------------------------------------------
            System.out.println("\n[8/11] 🛡️ Testando TB_GUARDIAO via GuardiaoDao...");
            carteiraGuardiao = new Carteira(0L, "0xGUARD_" + sufixo);
            carteiraDao.inserir(carteiraGuardiao);

            String cpfGuardiao = String.format("%011d", (long) (Math.abs((System.nanoTime() + 12345) % 100000000000L)));
            usuarioGuardiao = new Usuario(
                    0L,
                    "Dra. Beatriz Guardiã",
                    cpfGuardiao,
                    "beatriz." + sufixo + "@aurumconselho.com",
                    "$2a$12$hashGuardiaoSeguroSimulado1234567890",
                    LocalDate.of(1988, 3, 12),
                    carteiraGuardiao
            );
            usuarioDao.inserir(usuarioGuardiao);

            guardiao = new Guardiao(0L, usuarioGuardiao, empresa);
            guardiaoDao.inserir(guardiao);
            System.out.println("   [INSERT] Guardião corporativo nomeado! ID: " + guardiao.getId());

            Guardiao gConsultado = guardiaoDao.buscarPorId(guardiao.getId());
            System.out.println("   [SELECT] Guardião consultado: Usuário = " + gConsultado.getUsuarioResponsavel().getNomeCompleto() + " | Ativo = " + gConsultado.isAtivo());

            gConsultado.suspender();
            guardiaoDao.atualizar(gConsultado);
            Guardiao gAtualizado = guardiaoDao.buscarPorId(guardiao.getId());
            System.out.println("   [UPDATE] Status do guardião alterado! Ativo = " + gAtualizado.isAtivo());

            List<Guardiao> conselho = guardiaoDao.listarPorEmpresa(empresa.getId());
            System.out.println("   [COLLECTION] Conselho de guardiões da empresa: " + conselho.size() + " membros (ArrayList)");

            // -----------------------------------------------------------------
            // 9. TB_SOLICITACAO_TRANSACAO (SolicitacaoTransacaoDao)
            // -----------------------------------------------------------------
            System.out.println("\n[9/11] ✍️ Testando TB_SOLICITACAO_TRANSACAO via SolicitacaoTransacaoDao...");
            solicitacao = new SolicitacaoDeTransacao(
                    0L,
                    empresa,
                    transacao,
                    2,
                    LocalDateTime.now().plusDays(3)
            );
            solicitacao.setMotivoRejeicao("Nenhum");
            solicitacaoDao.inserir(solicitacao);
            System.out.println("   [INSERT] Solicitação multi-assinatura criada! ID: " + solicitacao.getId());

            SolicitacaoDeTransacao solConsultada = solicitacaoDao.buscarPorId(solicitacao.getId());
            System.out.println("   [SELECT] Solicitação recuperada: Quórum = " + solConsultada.getMinimoAprovacoesNecessarias() + " | Status = " + solConsultada.getStatus());

            solConsultada.setStatus(SolicitacaoDeTransacao.StatusSolicitacao.APROVADA);
            solicitacaoDao.atualizar(solConsultada);
            SolicitacaoDeTransacao solAtualizada = solicitacaoDao.buscarPorId(solicitacao.getId());
            System.out.println("   [UPDATE] Solicitação atualizada! Novo status = " + solAtualizada.getStatus());

            List<SolicitacaoDeTransacao> solicitacoes = solicitacaoDao.listarTodos();
            System.out.println("   [COLLECTION] Total de solicitações de governança: " + solicitacoes.size() + " (ArrayList)");

            // -----------------------------------------------------------------
            // 10. TB_AULA (AulaDao)
            // -----------------------------------------------------------------
            System.out.println("\n[10/11] 🎓 Testando TB_AULA via AulaDao...");
            int ordemAula = (int) (Math.abs(System.nanoTime() % 80000) + 1000);
            aula = new Aula(
                    0L,
                    "Introdução ao Aurum Custody",
                    "<p>Aprenda como funciona a custódia institucional e o modelo multi-assinatura.</p>",
                    ordemAula,
                    150
            );
            aulaDao.inserir(aula);
            System.out.println("   [INSERT] Módulo de aula inserido! ID: " + aula.getId() + " | Ordem: " + aula.getOrdem());

            Aula auConsultada = aulaDao.buscarPorId(aula.getId());
            System.out.println("   [SELECT] Aula consultada: Título = " + auConsultada.getTitulo() + " | Recompensa = " + auConsultada.getPontosXp() + " XP");

            auConsultada.setTitulo("Introdução ao Aurum Custody Avançado");
            auConsultada.setPontosXp(200);
            aulaDao.atualizar(auConsultada);
            Aula auAtualizada = aulaDao.buscarPorId(aula.getId());
            System.out.println("   [UPDATE] Aula atualizada! Novo título = " + auAtualizada.getTitulo() + " | XP = " + auAtualizada.getPontosXp());

            List<Aula> trilha = aulaDao.listarTodos();
            System.out.println("   [COLLECTION] Trilha pedagógica de aulas: " + trilha.size() + " módulos (ArrayList)");

            // -----------------------------------------------------------------
            // 11. TB_PROGRESSO_AULA (ProgressoUsuarioAulaDao)
            // -----------------------------------------------------------------
            System.out.println("\n[11/11] 🏆 Testando TB_PROGRESSO_AULA via ProgressoUsuarioAulaDao...");
            progresso = new ProgressoUsuarioAula(0L, usuarioMaster, aula);
            progressoDao.inserir(progresso);
            System.out.println("   [INSERT] Progresso registrado! ID: " + progresso.getId());

            ProgressoUsuarioAula prConsultado = progressoDao.buscarPorId(progresso.getId());
            System.out.println("   [SELECT] Progresso consultado: Aluno = " + prConsultado.getAluno().getNomeCompleto() + " | Concluída = " + prConsultado.isConcluida());

            prConsultado.marcarComoConcluida();
            progressoDao.atualizar(prConsultado);
            ProgressoUsuarioAula prAtualizado = progressoDao.buscarPorId(progresso.getId());
            System.out.println("   [UPDATE] Progresso concluído! Status = " + prAtualizado.isConcluida() + " | Data = " + prAtualizado.getDataConclusao());

            List<ProgressoUsuarioAula> progressosUser = progressoDao.listarPorUsuario(usuarioMaster.getId());
            System.out.println("   [COLLECTION] Histórico de aulas do usuário: " + progressosUser.size() + " registros (ArrayList)");

            // -----------------------------------------------------------------
            // TEARDOWN: EXCLUSÃO EM ORDEM INVERSA DE DEPENDÊNCIA (INTEGRIDADE REFERENCIAL)
            // -----------------------------------------------------------------
            System.out.println("\n🧹 Executando Limpeza Segura (DELETE) em ordem inversa de dependência...");

            progressoDao.excluir(progresso.getId());
            System.out.println("   [DELETE 11/11] TB_PROGRESSO_AULA ID " + progresso.getId() + " removido.");

            aulaDao.excluir(aula.getId());
            System.out.println("   [DELETE 10/11] TB_AULA ID " + aula.getId() + " removida.");

            solicitacaoDao.excluir(solicitacao.getId());
            System.out.println("   [DELETE 9/11] TB_SOLICITACAO_TRANSACAO ID " + solicitacao.getId() + " removida.");

            guardiaoDao.excluir(guardiao.getId());
            System.out.println("   [DELETE 8/11] TB_GUARDIAO ID " + guardiao.getId() + " removido.");

            usuarioDao.excluir(usuarioGuardiao.getId());
            carteiraDao.excluir(carteiraGuardiao.getId());
            System.out.println("   [DELETE 8b/11] Usuário e Carteira de Guardião de teste removidos.");

            cofreTemporalDao.excluir(cofre.getId());
            System.out.println("   [DELETE 7/11] TB_COFRE_TEMPORAL ID " + cofre.getId() + " removido.");

            transacaoDao.excluir(transacao.getId());
            System.out.println("   [DELETE 6/11] TB_TRANSACAO ID " + transacao.getId() + " removida.");

            posicaoCriptoDao.excluir(posicao.getId());
            System.out.println("   [DELETE 5/11] TB_POSICAO_CRIPTO ID " + posicao.getId() + " removida.");

            criptoativoDao.excluir(cripto.getId());
            System.out.println("   [DELETE 4/11] TB_CRIPTOATIVO ID " + cripto.getId() + " removido.");

            empresaDao.excluir(empresa.getId());
            carteiraDao.excluir(carteiraCorp.getId());
            System.out.println("   [DELETE 3/11] TB_EMPRESA ID " + empresa.getId() + " e Carteira Corporativa removidas.");

            usuarioDao.excluir(usuarioMaster.getId());
            carteiraDao.excluir(carteiraUser.getId());
            System.out.println("   [DELETE 2/11 e 1/11] TB_USUARIO ID " + usuarioMaster.getId() + " e TB_CARTEIRA ID " + carteiraUser.getId() + " removidos.");

            System.out.println("\n✅ TODAS AS 11 TABELAS FORAM TESTADAS COM SUCESSO NO ORACLE DATABASE (CRUD 100%)!");

        } catch (java.sql.SQLException e) {
            System.out.println("\n⚠️ [DIAGNÓSTICO JDBC] Operação no banco Oracle não concluída: " + e.getMessage());
            System.out.println("   Código do Erro Oracle: " + e.getErrorCode());
            System.out.println("   Nota: Certifique-se de que o acesso à rede da FIAP (VPN/Campus) está ativo para conectar em oracle.fiap.com.br.");
        } catch (Exception e) {
            System.out.println("\n❌ [ERRO INESPERADO] Falha durante a suíte de integração: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
