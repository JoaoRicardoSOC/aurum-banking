import br.com.jence.aurum.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

        System.out.println("\nTestando Cofre:");
        testarCofreTemporal();
        testarResgateAntecipado();

        System.out.println("\nTestando Combo:");
        testarComboValido();
        testarComboInvalido();

        System.out.println("\nTestando Transação:");
        testarTransacaoConcluida();
        testarFalhaEmTransacaoConcluida();

        System.out.println("\nTestando Solicitação:");
        testarSolicitacaoAprovada();
        testarVotoDuplicado();

        System.out.println("\nTestando Notificação:");
        testarNotificacao();

        System.out.println("\nTestando Relatório:");
        testarRelatorioFiscal();


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

    private static void testarComboInvalido() {
        System.out.println("\n--- testarComboInvalido ---");

        try {

            ComboCriptoativos combo = new ComboCriptoativos(
                    1L,
                    "Combo Inválido",
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
                            user,
                            null
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
}
