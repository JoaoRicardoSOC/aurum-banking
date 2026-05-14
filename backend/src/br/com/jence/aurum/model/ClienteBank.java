package br.com.jence.aurum.model;

import java.util.Objects;

public abstract class ClienteBank {
    protected Long id;
    protected Carteira carteira;

    public ClienteBank(Long id, Carteira carteira) {
        this.id = Objects.requireNonNull(id);
        this.carteira = Objects.requireNonNull(carteira);
    }

    public abstract String getDocumentoIdentificacao();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Carteira getCarteira() { return carteira; }
    public void setCarteira(Carteira carteira) { this.carteira = carteira; }
}
