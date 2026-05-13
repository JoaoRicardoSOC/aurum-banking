package br.com.jence.aurum.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Empresa {
    private final Long id;
    private String razaoSocial;
    private final String cnpj;
    private final LocalDate dataCadastro;
    private boolean statusAtivo;
    private final Carteira carteiraCorporativa;
    private Usuario usuarioMaster;
    private final List<Guardiao> conselhoGuardioes;
    private final List<RelatorioFiscal> historicoRelatorios;

    public Empresa(Long id, String razaoSocial, String cnpj, Usuario master, Carteira carteira) {
        this.id = Objects.requireNonNull(id);
        this.razaoSocial = Objects.requireNonNull(razaoSocial);
        this.cnpj = validarCnpj(cnpj);
        this.usuarioMaster = Objects.requireNonNull(master);
        this.carteiraCorporativa = Objects.requireNonNull(carteira);
        this.dataCadastro = LocalDate.now();
        this.statusAtivo = true;
        this.conselhoGuardioes = new ArrayList<>();
        this.historicoRelatorios = new ArrayList<>();
    }

    public void adicionarGuardiao(Guardiao guardiao) {
        Objects.requireNonNull(guardiao);
        if (this.conselhoGuardioes.contains(guardiao)) {
            throw new IllegalArgumentException("Guardião já registrado no conselho.");
        }
        this.conselhoGuardioes.add(guardiao);
    }

    public void registrarRelatorio(RelatorioFiscal relatorio) {
        Objects.requireNonNull(relatorio);
        this.historicoRelatorios.add(relatorio);
    }

    public void transferirMaster(Usuario novoMaster, SolicitacaoDeTransacao autorizacao) {
        if (autorizacao.getStatus() != SolicitacaoDeTransacao.StatusSolicitacao.APROVADA) {
            throw new SecurityException("Transferência de propriedade exige aprovação do conselho.");
        }
        this.usuarioMaster = Objects.requireNonNull(novoMaster);
    }

    public void suspenderEmpresa() {
        this.statusAtivo = false;
    }

    private String validarCnpj(String cnpj) {
        String cleaned = cnpj.replaceAll("\\D", "");
        if (cleaned.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido: deve conter 14 dígitos.");
        }
        return cleaned;
    }

    public List<Guardiao> getConselhoGuardioes() {
        return Collections.unmodifiableList(conselhoGuardioes);
    }

    public List<RelatorioFiscal> getHistoricoRelatorios() {
        return Collections.unmodifiableList(historicoRelatorios);
    }

    public Long getId() { return id; }
    public String getCnpj() { return cnpj; }
    public boolean isStatusAtivo() { return statusAtivo; }
    public Carteira getCarteiraCorporativa() { return carteiraCorporativa; }
    public Usuario getUsuarioMaster() { return usuarioMaster; }
}