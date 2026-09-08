package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transacao implements RegistroAuditavel {

    private Long id;
    private TipoTransacao tipoTransacao;
    private StatusTransacao status;
    private LocalDateTime dataHora;

    private BigDecimal valorMovimentadoBrl;
    private BigDecimal quantidadeCripto;
    private BigDecimal cotacaoNoMomento;
    private BigDecimal taxaOperacao;

    private Carteira carteiraVinculada;
    private Criptoativo moedaEnvolvida;

    public Transacao(Long id, TipoTransacao tipo, BigDecimal valorBrl, BigDecimal qtdeCripto,
                     BigDecimal cotacao, BigDecimal taxa, Carteira carteira, Criptoativo moeda) {
        this.id = Objects.requireNonNull(id);
        this.tipoTransacao = Objects.requireNonNull(tipo);

        this.valorMovimentadoBrl = validarValorPositivo(valorBrl, "Valor movimentado BRL");
        this.quantidadeCripto = validarValorPositivo(qtdeCripto, "Quantidade de cripto");
        this.cotacaoNoMomento = validarValorPositivo(cotacao, "Cotação no momento");
        this.taxaOperacao = validarValorPositivo(taxa, "Taxa da operação");

        this.carteiraVinculada = Objects.requireNonNull(carteira);
        this.moedaEnvolvida = moeda;
        this.dataHora = LocalDateTime.now();
        this.status = StatusTransacao.PENDENTE;
    }

    @Override
    public LocalDateTime getDataHoraRegistro() {
        return this.dataHora;
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
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public TipoTransacao getTipoTransacao() { return tipoTransacao; }
    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = Objects.requireNonNull(tipoTransacao);
    }

    public StatusTransacao getStatus() { return status; }
    public void setStatus(StatusTransacao status) {
        this.status = Objects.requireNonNull(status);
    }

    public LocalDateTime getDataHora() { return dataHora; }

    private void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public BigDecimal getValorMovimentadoBrl() { return valorMovimentadoBrl; }
    public void setValorMovimentadoBrl(BigDecimal valor) {
        this.valorMovimentadoBrl = validarValorPositivo(valor, "Valor Movimentado BRL");
    }

    public BigDecimal getQuantidadeCripto() { return quantidadeCripto; }
    public void setQuantidadeCripto(BigDecimal quantidadeCripto) {
        this.quantidadeCripto = validarValorPositivo(quantidadeCripto, "Quantidade Cripto");
    }

    public BigDecimal getCotacaoNoMomento() { return cotacaoNoMomento; }
    public void setCotacaoNoMomento(BigDecimal cotacaoNoMomento) {
        this.cotacaoNoMomento = validarValorPositivo(cotacaoNoMomento, "Cotação no Momento");
    }

    public BigDecimal getTaxaOperacao() { return taxaOperacao; }
    public void setTaxaOperacao(BigDecimal taxaOperacao) {
        this.taxaOperacao = validarValorPositivo(taxaOperacao, "Taxa de Operação");
    }

    public Carteira getCarteiraVinculada() { return carteiraVinculada; }
    public void setCarteiraVinculada(Carteira carteiraVinculada) {
        this.carteiraVinculada = Objects.requireNonNull(carteiraVinculada);
    }

    public Criptoativo getMoedaEnvolvida() { return moedaEnvolvida; }
    public void setMoedaEnvolvida(Criptoativo moedaEnvolvida) {
        this.moedaEnvolvida = moedaEnvolvida;
    }

    public enum TipoTransacao {
        COMPRA, VENDA, TRANSFERENCIA_INTERNA, DEPOSITO_FIAT, SAQUE_FIAT, PAGAMENTO_TAXA
    }

    public enum StatusTransacao {
        PENDENTE, PROCESSANDO, CONCLUIDA, FALHA, CANCELADA
    }
}