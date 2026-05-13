package br.com.jence.aurum.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Guardiao {
    private final Long id;
    private final LocalDate dataNomeacao;
    private boolean ativo;
    private final Usuario usuarioResponsavel;
    private final Empresa empresaProtegida;
    private final List<SolicitacaoDeTransacao> historicoAprovacoes;

    public Guardiao(Long id, Usuario usuario, Empresa empresa) {
        this.id = Objects.requireNonNull(id);
        this.usuarioResponsavel = Objects.requireNonNull(usuario);
        this.empresaProtegida = Objects.requireNonNull(empresa);
        this.dataNomeacao = LocalDate.now();
        this.ativo = true;
        this.historicoAprovacoes = new ArrayList<>();
    }

    public void votar(SolicitacaoDeTransacao solicitacao, boolean aprovado, String justificativa) {
        validarEstadoParaVoto();
        Objects.requireNonNull(solicitacao, "A solicitação não pode ser nula.");

        if (!aprovado && (justificativa == null || justificativa.isBlank())) {
            throw new IllegalArgumentException("Justificativa é obrigatória para rejeições.");
        }

        this.historicoAprovacoes.add(solicitacao);

        solicitacao.registrarVoto(this, aprovado, justificativa);
    }

    private void validarEstadoParaVoto() {
        if (!ativo) {
            throw new IllegalStateException("Guardião inativo não pode realizar votações.");
        }
        if (!empresaProtegida.isStatusAtivo()) {
            throw new IllegalStateException("A empresa protegida está suspensa.");
        }
    }

    public void suspender() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }

    public List<SolicitacaoDeTransacao> getHistoricoAprovacoes() {
        return Collections.unmodifiableList(historicoAprovacoes);
    }

    public Long getId() { return id; }
    public boolean isAtivo() { return ativo; }
    public Usuario getUsuarioResponsavel() { return usuarioResponsavel; }
    public Empresa getEmpresaProtegida() { return empresaProtegida; }
    public LocalDate getDataNomeacao() { return dataNomeacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guardiao guardiao)) return false;
        return Objects.equals(id, guardiao.id) &&
                Objects.equals(usuarioResponsavel, guardiao.usuarioResponsavel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioResponsavel);
    }
}