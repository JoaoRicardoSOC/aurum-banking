package br.com.jence.aurum.model;

import java.util.Objects;

public abstract class ProdutoAurum {
    protected Long id;
    protected String nome;
    protected boolean disponivel;

    public ProdutoAurum(Long id, String nome) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.disponivel = false;
    }

    public abstract void validarDisponibilidade();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Objects.requireNonNull(nome); }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}