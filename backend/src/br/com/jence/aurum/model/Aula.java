package br.com.jence.aurum.model;

public class Aula {
    private Long id;
    private String titulo;
    private String conteudoHtml;
    private int ordem;
    private int pontosXp;

    public Aula() {
    }

    public Aula(Long id, String titulo, String conteudoHtml, int ordem, int pontosXp) {
        this.id = id;
        this.titulo = titulo;
        this.conteudoHtml = conteudoHtml;
        this.ordem = ordem;
        this.pontosXp = pontosXp;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudoHtml() {
        return conteudoHtml;
    }

    public void setConteudoHtml(String conteudoHtml) {
        this.conteudoHtml = conteudoHtml;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getPontosXp() {
        return pontosXp;
    }

    public void setPontosXp(int pontosXp) {
        this.pontosXp = pontosXp;
    }
}