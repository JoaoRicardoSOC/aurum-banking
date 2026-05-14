package br.com.jence.aurum.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Empresa extends ClienteBank {

    private String razaoSocial;
    private String cnpj;
    private LocalDate dataCadastro;
    private boolean statusAtivo;
    private Usuario usuarioMaster;
    private List<Guardiao> conselhoGuardioes;
    private List<RelatorioFiscal> historicoRelatorios;

    public Empresa(Long id, String razaoSocial, String cnpj, Usuario master, Carteira carteira) {
        // 1. Herança: Inicializa ID e Carteira na Superclasse ClienteBank
        super(id, carteira);

        this.razaoSocial = Objects.requireNonNull(razaoSocial, "Razão social é obrigatória.");
        this.cnpj = validarCnpj(cnpj);
        this.usuarioMaster = Objects.requireNonNull(master, "Usuário master é obrigatório.");
        this.dataCadastro = LocalDate.now();
        this.statusAtivo = true;
        this.conselhoGuardioes = new ArrayList<>();
        this.historicoRelatorios = new ArrayList<>();
    }

    // 2. POLIMORFISMO DINÂMICO (Override)
    @Override
    public String getDocumentoIdentificacao() {
        return this.cnpj; // Identificação única da Pessoa Jurídica
    }

    // --- MÉTODOS DE NEGÓCIO ---

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

    public void transferirMaster(Usuario novoMaster) {
        // Trava de Segurança mantida conforme nossa auditoria
        if (this.statusAtivo) {
            throw new IllegalStateException("ALERTA DE SEGURANÇA: A transferência exige que a empresa seja suspensa.");
        }
        this.usuarioMaster = Objects.requireNonNull(novoMaster, "O novo usuário master é obrigatório.");
    }

    public void suspenderEmpresa() {
        this.statusAtivo = false;
    }

    private String validarCnpj(String cnpj) {
        String cleaned = Objects.requireNonNull(cnpj).replaceAll("\\D", "");
        if (cleaned.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido: deve conter 14 dígitos.");
        }
        return cleaned;
    }

    // --- GETTERS E SETTERS BLINDADOS (FASE 3) ---

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = Objects.requireNonNull(razaoSocial, "Razão social não pode ser vazia.");
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) {
        this.cnpj = validarCnpj(cnpj);
    }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public boolean isStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(boolean statusAtivo) { this.statusAtivo = statusAtivo; }

    public Usuario getUsuarioMaster() { return usuarioMaster; }
    public void setUsuarioMaster(Usuario usuarioMaster) {
        if (this.statusAtivo) {
            throw new IllegalStateException("SEGURANÇA: Utilize o método transferirMaster() com a empresa suspensa.");
        }
        this.usuarioMaster = Objects.requireNonNull(usuarioMaster);
    }

    public List<Guardiao> getConselhoGuardioes() {
        return Collections.unmodifiableList(conselhoGuardioes);
    }
    public void setConselhoGuardioes(List<Guardiao> conselhoGuardioes) {
        this.conselhoGuardioes = new ArrayList<>(Objects.requireNonNull(conselhoGuardioes));
    }

    public List<RelatorioFiscal> getHistoricoRelatorios() {
        return Collections.unmodifiableList(historicoRelatorios);
    }
    public void setHistoricoRelatorios(List<RelatorioFiscal> historicoRelatorios) {
        this.historicoRelatorios = new ArrayList<>(Objects.requireNonNull(historicoRelatorios));
    }
}