package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Criptoativo {

    private Long id;
    private String nome;
    private String sigla;
    private String logoUrl;
    private BigDecimal precoAtualBrl;
    private BigDecimal variacao24h;
    private boolean ativoParaNegociacao;

    public Criptoativo(Long id, String nome, String sigla, BigDecimal precoInicial) {
        this.id = Objects.requireNonNull(id);
        this.nome = validarTexto(nome, "Nome");
        this.sigla = validarTexto(sigla, "Sigla").toUpperCase();

        validarPreco(precoInicial);
        this.precoAtualBrl = precoInicial;
        this.variacao24h = BigDecimal.ZERO;
        this.ativoParaNegociacao = true;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        validarPreco(novoPreco);

        if (this.precoAtualBrl.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal diferenca = novoPreco.subtract(this.precoAtualBrl);
            this.variacao24h = diferenca
                    .divide(this.precoAtualBrl, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        } else {
            this.variacao24h = BigDecimal.ZERO;
        }

        this.precoAtualBrl = novoPreco;
    }

    public void suspenderNegociacao() {
        this.ativoParaNegociacao = false;
    }

    public void ativarNegociacao() {
        this.ativoParaNegociacao = true;
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço do criptoativo não pode ser negativo ou nulo.");
        }
    }

    private String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
        return texto.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        this.nome = validarTexto(nome, "Nome");
    }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) {
        this.sigla = validarTexto(sigla, "Sigla").toUpperCase();
    }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public BigDecimal getPrecoAtualBrl() { return precoAtualBrl; }

    private void setPrecoAtualBrl(BigDecimal precoAtualBrl) {
        this.precoAtualBrl = precoAtualBrl;
    }

    public BigDecimal getVariacao24h() { return variacao24h; }

    private void setVariacao24h(BigDecimal variacao24h) {
        this.variacao24h = variacao24h;
    }

    public boolean isAtivoParaNegociacao() { return ativoParaNegociacao; }
    public void setAtivoParaNegociacao(boolean ativoParaNegociacao) {
        this.ativoParaNegociacao = ativoParaNegociacao;
    }

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