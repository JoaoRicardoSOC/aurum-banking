package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transacao {
    private final Long id;
    private final TipoTransacao tipoTransacao;
    private StatusTransacao status;
    private final LocalDateTime dataHora;

    private final BigDecimal valorMovimentadoBrl;
    private final BigDecimal quantidadeCripto;
    private final BigDecimal cotacaoNoMomento;
    private final BigDecimal taxaOperacao;

    private final Carteira carteiraVinculada;
    private final Criptoativo moedaEnvolvida;

    public Transacao(Long id, TipoTransacao tipo, BigDecimal valorBrl, BigDecimal qtdeCripto,
                     BigDecimal cotacao, BigDecimal taxa, Carteira carteira, Criptoativo moeda) {
        this.id = Objects.requireNonNull(id);
        this.tipoTransacao = Objects.requireNonNull(tipo);

        this.valorMovimentadoBrl = validarValorPositivo(valorBrl, "Valor movimentado BRL");
        this.quantidadeCripto = validarValorPositivo(qtdeCripto, "Quantidade de cripto");
        this.cotacaoNoMomento = validarValorPositivo(cotacao, "Cotação no momento");
        this.taxaOperacao = validarValorPositivo(taxa, "Taxa da operação");

        this.carteiraVinculada = Objects.requireNonNull(carteira);
        this.moedaEnvolvida = Objects.requireNonNull(moeda);

        this.dataHora = LocalDateTime.now();
        this.status = StatusTransacao.PENDENTE;
    }

    public void marcarComoConcluida() {
        if (this.status == StatusTransacao.FALHA || this.status == StatusTransacao.CANCELADA) {
            throw new IllegalStateException("Não é possível concluir uma transação falha ou cancelada.");
        }
        this.status = StatusTransacao.CONCLUIDA;
    }

    public void registrarFalha() {
        if (this.status == StatusTransacao.CONCLUIDA) {
            throw new IllegalStateException("Transação já concluída não pode falhar.");
        }
        this.status = StatusTransacao.FALHA;
    }

    private BigDecimal validarValorPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(campo + " não pode ser nulo ou negativo.");
        }
        return valor;
    }

    public Long getId() { return id; }
    public StatusTransacao getStatus() { return status; }
    public TipoTransacao getTipoTransacao() { return tipoTransacao; }
    public BigDecimal getValorMovimentadoBrl() { return valorMovimentadoBrl; }
    public BigDecimal getQuantidadeCripto() { return quantidadeCripto; }

    public enum TipoTransacao {
        COMPRA, VENDA, TRANSFERENCIA_INTERNA, DEPOSITO_FIAT, SAQUE_FIAT, PAGAMENTO_TAXA
    }

    public enum StatusTransacao {
        PENDENTE, PROCESSANDO, CONCLUIDA, FALHA, CANCELADA
    }
}