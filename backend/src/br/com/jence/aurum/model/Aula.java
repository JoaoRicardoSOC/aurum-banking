package br.com.jence.aurum.model;

import java.util.Objects;

public class Aula {

    private Long id;
    private String titulo;
    private String conteudoHtml;
    private int ordem;
    private int pontosXp;

    public Aula() {
    }

    public Aula(Long id, String titulo, String conteudoHtml, int ordem, int pontosXp) {
        this.id = Objects.requireNonNull(id, "O ID da aula é obrigatório.");
        this.titulo = validarTexto(titulo, "Título");
        this.conteudoHtml = validarTexto(conteudoHtml, "Conteúdo HTML");
        this.ordem = validarNumeroPositivo(ordem, "Ordem da aula");
        this.pontosXp = validarNumeroPositivo(pontosXp, "Pontos XP");
    }

    public void iniciarAula() {
        System.out.println("Iniciando a aula: " + this.titulo);
    }

    public int finalizarAula() {
        System.out.println("Aula '" + this.titulo + "' finalizada!");
        System.out.println("Você ganhou " + this.pontosXp + " XP.");
        return this.pontosXp;
    }

    public void exibirResumo() {
        System.out.println("Aula " + this.ordem + ": " + this.titulo + " (" + this.pontosXp + " XP)");
    }

    private String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
        return texto.trim();
    }

    private int validarNumeroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " deve ser estritamente maior que zero.");
        }
        return valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        this.titulo = validarTexto(titulo, "Título");
    }

    public String getConteudoHtml() { return conteudoHtml; }
    public void setConteudoHtml(String conteudoHtml) {
        this.conteudoHtml = validarTexto(conteudoHtml, "Conteúdo HTML");
    }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) {
        this.ordem = validarNumeroPositivo(ordem, "Ordem da aula");
    }

    public int getPontosXp() { return pontosXp; }
    public void setPontosXp(int pontosXp) {
        this.pontosXp = validarNumeroPositivo(pontosXp, "Pontos XP");
    }
}