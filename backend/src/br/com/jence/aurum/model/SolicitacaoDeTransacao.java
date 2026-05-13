package br.com.jence.aurum.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SolicitacaoDeTransacao {
    private final Long id;
    private final LocalDateTime dataCriacao;
    private final LocalDateTime dataExpiracao;
    private final Transacao transacaoPendente;
    private final int minimoAprovacoesNecessarias;
    private final List<Guardiao> guardioesQueAprovaram;
    private final List<Guardiao> guardioesQueRejeitaram;
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
        if (guardioesQueAprovaram.size() >= minimoAprovacoesNecessarias) {
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

    public Long getId() { return id; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public String getMotivoRejeicao() { return motivoRejeicao; }
    public List<Guardiao> getGuardioesQueAprovaram() { return Collections.unmodifiableList(guardioesQueAprovaram); }
    public List<Guardiao> getGuardioesQueRejeitaram() { return Collections.unmodifiableList(guardioesQueRejeitaram); }
    public StatusSolicitacao getStatus() { return status; }
    public Transacao getTransacaoPendente() { return transacaoPendente; }

    public enum StatusSolicitacao { PENDENTE, APROVADA, REJEITADA, EXPIRADA, CANCELADA }
}