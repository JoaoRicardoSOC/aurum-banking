package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.util.Objects;

public class PosicaoCripto {
    
    private Long id; 
    private Criptoativo moeda;
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
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public Criptoativo getMoeda() { return moeda; }
    public void setMoeda(Criptoativo moeda) { 
        this.moeda = Objects.requireNonNull(moeda, "A moeda não pode ser nula."); 
    }

    public BigDecimal getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(BigDecimal quantidadeTotal) { 
        if (quantidadeTotal == null || quantidadeTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
        this.quantidadeTotal = quantidadeTotal; 
    }

    @Override
    public String toString() {
        return "PosicaoCripto{" +
                "sigla='" + moeda.getSigla() + '\'' +
                ", quantidade=" + quantidadeTotal +
                '}';
    }
}