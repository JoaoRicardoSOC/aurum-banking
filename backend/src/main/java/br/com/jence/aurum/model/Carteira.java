package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Carteira {
    private Long id;
    private String enderecoDigital;
    private BigDecimal saldoDisponivelBrl;

    private List<PosicaoCripto> ativosAdquiridos;
    private List<Transacao> historicoTransacoes;
    private List<CofreTemporal> cofresAtivos;

    public Carteira(Long id, String enderecoDigital) {
        this.id = Objects.requireNonNull(id, "ID da carteira é obrigatório.");
        this.enderecoDigital = validarEndereco(enderecoDigital);
        this.saldoDisponivelBrl = BigDecimal.ZERO;

        this.ativosAdquiridos = new ArrayList<>();
        this.historicoTransacoes = new ArrayList<>();
        this.cofresAtivos = new ArrayList<>();
    }

    public synchronized void depositarBrl(BigDecimal valor) {
        validarOperacao(valor, "Depósito");
        this.saldoDisponivelBrl = this.saldoDisponivelBrl.add(valor);
    }

    public synchronized void depositarBrl(BigDecimal valor, String origemRecurso) {
        validarOperacao(valor, "Depósito com Justificativa");
        System.out.println("COMPLIANCE - Origem do recurso: " + origemRecurso);
        this.saldoDisponivelBrl = this.saldoDisponivelBrl.add(valor);
    }

    public synchronized void sacarBrl(BigDecimal valor) {
        validarOperacao(valor, "Saque");
        if (this.saldoDisponivelBrl.compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente para realizar o saque.");
        }
        this.saldoDisponivelBrl = this.saldoDisponivelBrl.subtract(valor);
    }

    public void registrarTransacao(Transacao transacao) {
        this.historicoTransacoes.add(Objects.requireNonNull(transacao));
    }

    public void adicionarCofreTemporal(CofreTemporal cofre) {
        this.cofresAtivos.add(Objects.requireNonNull(cofre));
    }

    public void atualizarPosicaoCripto(Criptoativo moeda, BigDecimal quantidade, boolean isCompra) {
        PosicaoCripto posicao = ativosAdquiridos.stream()
                .filter(p -> p.getMoeda().equals(moeda))
                .findFirst()
                .orElse(null);

        if (posicao == null) {
            if (!isCompra) throw new IllegalStateException("Não possui saldo para vender.");
            ativosAdquiridos.add(new PosicaoCripto(System.nanoTime(), moeda, quantidade));
        } else {
            if (isCompra) posicao.adicionarQuantidade(quantidade);
            else posicao.subtrairQuantidade(quantidade);
        }
    }

    private void validarOperacao(BigDecimal valor, String tipoOperacao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(tipoOperacao + ": O valor deve ser estritamente maior que zero.");
        }
    }

    private String validarEndereco(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("O endereço digital não pode ser vazio.");
        }
        return endereco.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public String getEnderecoDigital() { return enderecoDigital; }
    public void setEnderecoDigital(String enderecoDigital) {
        this.enderecoDigital = validarEndereco(enderecoDigital);
    }

    public BigDecimal getSaldoDisponivelBrl() { return saldoDisponivelBrl; }

    private void setSaldoDisponivelBrl(BigDecimal saldoDisponivelBrl) {
        this.saldoDisponivelBrl = saldoDisponivelBrl;
    }

    public List<PosicaoCripto> getAtivosAdquiridos() {
        return Collections.unmodifiableList(ativosAdquiridos);
    }
    public void setAtivosAdquiridos(List<PosicaoCripto> ativosAdquiridos) {
        this.ativosAdquiridos = new ArrayList<>(Objects.requireNonNull(ativosAdquiridos));
    }

    public List<Transacao> getHistoricoTransacoes() {
        return Collections.unmodifiableList(historicoTransacoes);
    }
    public void setHistoricoTransacoes(List<Transacao> historicoTransacoes) {
        this.historicoTransacoes = new ArrayList<>(Objects.requireNonNull(historicoTransacoes));
    }

    public List<CofreTemporal> getCofresAtivos() {
        return Collections.unmodifiableList(cofresAtivos);
    }
    public void setCofresAtivos(List<CofreTemporal> cofresAtivos) {
        this.cofresAtivos = new ArrayList<>(Objects.requireNonNull(cofresAtivos));
    }
}