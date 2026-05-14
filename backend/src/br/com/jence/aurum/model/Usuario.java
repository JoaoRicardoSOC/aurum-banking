package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Usuario extends ClienteBank {

    private String nomeCompleto;
    private String cpf;
    private String email;
    private String senhaHash;
    private LocalDate dataNascimento;

    private ModoInterface modoInterface;
    private BigDecimal limiteOperacionalMensal;
    private boolean kycAprovado;

    private List<ProgressoUsuarioAula> historicoAulas;

    public Usuario(Long id, String nomeCompleto, String cpf, String email, String senhaHash,
                   LocalDate dataNascimento, Carteira carteira) {
        super(id, carteira);

        this.nomeCompleto = validarTexto(nomeCompleto, "Nome completo");
        this.cpf = validarCpf(cpf);
        this.email = validarEmail(email);
        this.senhaHash = Objects.requireNonNull(senhaHash);

        this.dataNascimento = validarMaioridade(dataNascimento);

        this.modoInterface = ModoInterface.INICIANTE;
        this.limiteOperacionalMensal = new BigDecimal("5000.00");
        this.kycAprovado = false;
        this.historicoAulas = new ArrayList<>();
    }

    @Override
    public String getDocumentoIdentificacao() {
        return this.cpf;
    }

    public void aprovarKyc(BigDecimal limiteInicial) {
        if (limiteInicial == null || limiteInicial.compareTo(limiteOperacionalMensal) < 0) {
            throw new IllegalArgumentException("O limite inicial deve ser positivo.");
        }
        this.kycAprovado = true;
        this.limiteOperacionalMensal = limiteInicial;
    }

    public boolean validarLimiteOperacional(BigDecimal valorDaOperacao) {
        if (this.kycAprovado) {
            return true;
        }
        return valorDaOperacao.compareTo(this.limiteOperacionalMensal) <= 0;
    }

    public void ajustarLimiteOperacional(BigDecimal novoLimite) {
        if (!this.kycAprovado) {
            throw new IllegalStateException("Não é possível ajustar limite de usuário sem KYC aprovado.");
        }
        if (novoLimite == null || novoLimite.compareTo(limiteOperacionalMensal) < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        this.limiteOperacionalMensal = novoLimite;
    }

    public void registrarProgressoAula(ProgressoUsuarioAula progresso) {
        Objects.requireNonNull(progresso);
        if (!this.historicoAulas.contains(progresso)) {
            this.historicoAulas.add(progresso);
        }
    }

    public void atualizarSenha(String novoHash) {
        this.senhaHash = Objects.requireNonNull(novoHash, "O hash da senha não pode ser nulo.");
    }

    private LocalDate validarMaioridade(LocalDate nascimento) {
        Objects.requireNonNull(nascimento, "Data de nascimento é obrigatória.");
        if (Period.between(nascimento, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Usuário deve ser maior de 18 anos para operar ativos.");
        }
        return nascimento;
    }

    private String validarCpf(String cpf) {
        String cleaned = Objects.requireNonNull(cpf).replaceAll("\\D", "");
        if (cleaned.length() != 11) {
            throw new IllegalArgumentException("CPF inválido: deve conter 11 dígitos.");
        }
        return cleaned;
    }

    private String validarEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        return email.toLowerCase().trim();
    }

    private String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
        return texto.trim();
    }

    public enum ModoInterface {
        INICIANTE, AVANCADO
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = validarTexto(nomeCompleto, "Nome completo");
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        this.cpf = validarCpf(cpf);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = validarEmail(email);
    }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) {
        this.senhaHash = Objects.requireNonNull(senhaHash, "O hash da senha não pode ser nulo.");
    }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = validarMaioridade(dataNascimento);
    }

    public ModoInterface getModoInterface() { return modoInterface; }
    public void setModoInterface(ModoInterface modoInterface) {
        this.modoInterface = Objects.requireNonNull(modoInterface);
    }

    public BigDecimal getLimiteOperacionalMensal() { return limiteOperacionalMensal; }
    public void setLimiteOperacionalMensal(BigDecimal limiteOperacionalMensal) {
        if (limiteOperacionalMensal == null || limiteOperacionalMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Integridade de dados: O limite não pode ser negativo.");
        }
        this.limiteOperacionalMensal = limiteOperacionalMensal;
    }

    public boolean isKycAprovado() { return kycAprovado; }
    public void setKycAprovado(boolean kycAprovado) { this.kycAprovado = kycAprovado; }

    public List<ProgressoUsuarioAula> getHistoricoAulas() {
        return Collections.unmodifiableList(historicoAulas);
    }
    public void setHistoricoAulas(List<ProgressoUsuarioAula> historicoAulas) {
        // Garante que a lista setada externamente possa receber novos itens futuramente (.add)
        this.historicoAulas = new ArrayList<>(Objects.requireNonNull(historicoAulas));
    }
}