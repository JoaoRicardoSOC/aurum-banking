package br.com.jence.aurum.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SolicitacaoDeTransacao implements RegistroAuditavel {

    private Long id;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao;
    private Transacao transacaoPendente;
    private int minimoAprovacoesNecessarias;
    private List<Guardiao> guardioesQueAprovaram;
    private List<Guardiao> guardioesQueRejeitaram;
    private StatusSolicitacao status;
    private String motivoRejeicao;

    public SolicitacaoDeTransacao(Long id, Transacao transacao, int quorum, LocalDateTime expiracao) {
        this.id = Objects.requireNonNull(id);
        this.dataCriacao = LocalDateTime.now();
        this.transacaoPendente = Objects.requireNonNull(transacao);
        this.minimoAprovacoesNecessarias = validarQuorum(quorum);
        this.dataExpiracao = Objects.requireNonNull(expiracao);
        this.guardioesQueAprovaram = new ArrayList<>();
        this.guardioesQueRejeitaram = new ArrayList<>();
        this.status = StatusSolicitacao.PENDENTE;
    }

    @Override
    public LocalDateTime getDataHoraRegistro() {
        return this.dataCriacao;
    }

    public synchronized void registrarVoto(Guardiao guardiao, boolean aprovado, String justificativa) {
        validarVotacao(guardiao);

        if (aprovado) {
            guardioesQueAprovaram.add(guardiao);
        } else {
            guardioesQueRejeitaram.add(guardiao);
            rejeitarSolicitacao(justificativa);
            return;
        }
        verificarQuorumAtingido();
    }

    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }

    private void verificarQuorumAtingido() {
        if (this.status == StatusSolicitacao.PENDENTE && guardioesQueAprovaram.size() >= minimoAprovacoesNecessarias) {
            this.status = StatusSolicitacao.APROVADA;
        }
    }

    private void rejeitarSolicitacao(String motivo) {
        this.status = StatusSolicitacao.REJEITADA;
        if (this.motivoRejeicao == null) {
            this.motivoRejeicao = motivo;
        } else {
            this.motivoRejeicao += " | " + motivo;
        }
    }

    private void validarVotacao(Guardiao guardiao) {
        if (isExpirada()) {
            this.status = StatusSolicitacao.EXPIRADA;
            throw new IllegalStateException("Solicitação expirada.");
        }
        if (this.status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Solicitação já finalizada: " + status);
        }
        if (guardioesQueAprovaram.contains(guardiao) || guardioesQueRejeitaram.contains(guardiao)) {
            throw new IllegalArgumentException("Guardião já votou nesta solicitação.");
        }
    }

    private int validarQuorum(int q) {
        if (q <= 0) throw new IllegalArgumentException("Quórum mínimo deve ser maior que zero.");
        return q;
    }

    public enum StatusSolicitacao { PENDENTE, APROVADA, REJEITADA, EXPIRADA, CANCELADA }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    private void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = Objects.requireNonNull(dataExpiracao);
        if (isExpirada() && this.status == StatusSolicitacao.PENDENTE) {
            this.status = StatusSolicitacao.EXPIRADA;
        }
    }

    public Transacao getTransacaoPendente() { return transacaoPendente; }
    public void setTransacaoPendente(Transacao transacaoPendente) {
        this.transacaoPendente = Objects.requireNonNull(transacaoPendente);
    }

    public int getMinimoAprovacoesNecessarias() { return minimoAprovacoesNecessarias; }
    public void setMinimoAprovacoesNecessarias(int minimoAprovacoesNecessarias) {
        this.minimoAprovacoesNecessarias = validarQuorum(minimoAprovacoesNecessarias);
        verificarQuorumAtingido();
    }

    public List<Guardiao> getGuardioesQueAprovaram() {
        return Collections.unmodifiableList(guardioesQueAprovaram);
    }
    public void setGuardioesQueAprovaram(List<Guardiao> guardioesQueAprovaram) {
        this.guardioesQueAprovaram = new ArrayList<>(Objects.requireNonNull(guardioesQueAprovaram));
        verificarQuorumAtingido();
    }

    public List<Guardiao> getGuardioesQueRejeitaram() {
        return Collections.unmodifiableList(guardioesQueRejeitaram);
    }
    public void setGuardioesQueRejeitaram(List<Guardiao> guardioesQueRejeitaram) {
        this.guardioesQueRejeitaram = new ArrayList<>(Objects.requireNonNull(guardioesQueRejeitaram));
    }

    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) {
        this.status = Objects.requireNonNull(status);
    }

    public String getMotivoRejeicao() { return motivoRejeicao; }
    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}