package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.util.Objects;

public class PosicaoCripto {
    private final Long id;
    private final Criptoativo moeda;
    private BigDecimal quantidadeTotal;

    public PosicaoCripto(Long id, Criptoativo moeda, BigDecimal quantidadeInicial) {
        this.id = Objects.requireNonNull(id);
        this.moeda = Objects.requireNonNull(moeda);
        this.quantidadeTotal = quantidadeInicial != null ? quantidadeInicial : BigDecimal.ZERO;
    }

    public synchronized void adicionarQuantidade(BigDecimal valor) {
        this.quantidadeTotal = this.quantidadeTotal.add(valor);
    }

    public synchronized void subtrairQuantidade(BigDecimal valor) {
        if (this.quantidadeTotal.compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente da criptomoeda.");
        }
        this.quantidadeTotal = this.quantidadeTotal.subtract(valor);
    }

    public Long getId() { return id; }
    public Criptoativo getMoeda() { return moeda; }
    public BigDecimal getQuantidadeTotal() { return quantidadeTotal; }

    @Override
    public String toString() {
        return "PosicaoCripto{" +
                "sigla='" + moeda.getSigla() + '\'' +
                ", quantidade=" + quantidadeTotal +
                '}';
    }
}