import br.com.jence.aurum.model.*;
import br.com.jence.aurum.service.TransacaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
