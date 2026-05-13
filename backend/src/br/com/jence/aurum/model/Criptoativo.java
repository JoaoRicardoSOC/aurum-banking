package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Criptoativo {
    private final Long id;
    private final String nome;
    private final String sigla;
    private String logoUrl;
    private BigDecimal precoAtualBrl;
    private BigDecimal variacao24h;
    private boolean ativoParaNegociacao;

    public Criptoativo(Long id, String nome, String sigla, BigDecimal precoInicial) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome).trim();
        this.sigla = Objects.requireNonNull(sigla).toUpperCase().trim();
        validarPreco(precoInicial);
        this.precoAtualBrl = precoInicial;
        this.variacao24h = BigDecimal.ZERO;
        this.ativoParaNegociacao = true;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        validarPreco(novoPreco);

        if (this.precoAtualBrl.compareTo(BigDecimal.ZERO) == 0) {
            this.variacao24h = BigDecimal.ZERO
                    .divide(this.precoAtualBrl, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        this.precoAtualBrl = novoPreco;
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço do criptoativo não pode ser negativo ou nulo.");
        }
    }

    public void suspenderNegociacao() {
        this.ativoParaNegociacao = false;
    }

    public void ativarNegociacao() {
        this.ativoParaNegociacao = true;
    }

    public Long getId() { return id; }
    public String getSigla() { return sigla; }
    public BigDecimal getPrecoAtualBrl() { return precoAtualBrl; }
    public BigDecimal getVariacao24h() { return variacao24h; }
    public boolean isAtivoParaNegociacao() { return ativoParaNegociacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Criptoativo that = (Criptoativo) o;
        return Objects.equals(id, that.id) && Objects.equals(sigla, that.sigla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sigla);
    }
}