package br.com.jence.aurum.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProgressoUsuarioAula {
    private Long id;
    private Usuario aluno;
    private Aula aulaAssistida;
    private boolean concluida;
    private LocalDateTime dataConclusao;

    public ProgressoUsuarioAula(Long id, Usuario aluno, Aula aulaAssistida) {
        this(id, aluno, aulaAssistida, false, null);
    }

    public ProgressoUsuarioAula(Long id, Usuario aluno, Aula aulaAssistida, boolean concluida, LocalDateTime dataConclusao) {
        this.id = Objects.requireNonNull(id);
        this.aluno = Objects.requireNonNull(aluno, "Aluno é obrigatório.");
        this.aulaAssistida = Objects.requireNonNull(aulaAssistida, "Aula é obrigatória.");
        this.concluida = concluida;
        this.dataConclusao = dataConclusao;
    }

    public void marcarComoConcluida() {
        if (!this.concluida) {
            this.concluida = true;
            this.dataConclusao = LocalDateTime.now();
        }
    }

    public void reverterConclusao() {
        this.concluida = false;
        this.dataConclusao = null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = Objects.requireNonNull(aluno); }

    public Aula getAulaAssistida() { return aulaAssistida; }
    public void setAulaAssistida(Aula aulaAssistida) { this.aulaAssistida = Objects.requireNonNull(aulaAssistida); }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgressoUsuarioAula that)) return false;
        return Objects.equals(aluno, that.aluno) &&
                Objects.equals(aulaAssistida, that.aulaAssistida);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aluno, aulaAssistida);
    }
}