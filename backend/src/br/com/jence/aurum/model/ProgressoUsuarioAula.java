package br.com.jence.aurum.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProgressoUsuarioAula {
    private final Long id;
    private final Usuario aluno;
    private final Aula aulaAssistida;
    private boolean concluida;
    private LocalDateTime dataConclusao;

    public ProgressoUsuarioAula(Long id, Usuario aluno, Aula aulaAssistida) {
        this.id = Objects.requireNonNull(id);
        this.aluno = Objects.requireNonNull(aluno, "Aluno é obrigatório.");
        this.aulaAssistida = Objects.requireNonNull(aulaAssistida, "Aula é obrigatória.");
        this.concluida = false;
        this.dataConclusao = null;
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
    public Usuario getAluno() { return aluno; }
    public Aula getAulaAssistida() { return aulaAssistida; }
    public boolean isConcluida() { return concluida; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }

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