package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class RelatorioFiscal implements RegistroAuditavel {

    private Long id;
    private LocalDateTime dataSolicitacao;
    private LocalDate dataInicioPeriodo;
    private LocalDate dataFimPeriodo;

    private BigDecimal saldoPatrimonialInicial;
    private BigDecimal saldoPatrimonialFinal;
    private BigDecimal totalLucroBrl;
    private BigDecimal totalPrejuizoBrl;
    private BigDecimal volumeTotalNegociado;

    private FormatoArquivo tipoFormato;
    private String urlArquivoGerado;
    private StatusRelatorio status;

    private Usuario solicitantePessoaFisica;
    private Empresa solicitantePessoaJuridica;

    public RelatorioFiscal(Long id, LocalDate inicio, LocalDate fim, FormatoArquivo formato, Usuario pf) {
        this(id, inicio, fim, formato, Objects.requireNonNull(pf, "Usuário não pode ser nulo"), null);
    }

    public RelatorioFiscal(Long id, LocalDate inicio, LocalDate fim, FormatoArquivo formato, Empresa pj) {
        this(id, inicio, fim, formato, null, Objects.requireNonNull(pj, "Empresa não pode ser nula"));
    }

    private RelatorioFiscal(Long id, LocalDate inicio, LocalDate fim, FormatoArquivo formato,
                           Usuario pf, Empresa pj) {
        this.id = Objects.requireNonNull(id);
        this.dataSolicitacao = LocalDateTime.now();
        this.dataInicioPeriodo = Objects.requireNonNull(inicio);
        this.dataFimPeriodo = Objects.requireNonNull(fim);
        this.tipoFormato = Objects.requireNonNull(formato);

        validarPeriodo(inicio, fim);
        validarSolicitanteXor(pf, pj);

        this.solicitantePessoaFisica = pf;
        this.solicitantePessoaJuridica = pj;
        this.status = StatusRelatorio.PROCESSANDO;

        inicializarValoresMonetarios();
    }

    @Override
    public LocalDateTime getDataHoraRegistro() {
        return this.dataSolicitacao;
    }

    public void finalizarProcessamento(BigDecimal saldoInicial, BigDecimal saldoFinal,
                                       BigDecimal lucro, BigDecimal prejuizo,
                                       BigDecimal volume, String urlAWSouGCP) {
        if (this.status != StatusRelatorio.PROCESSANDO) {
            throw new IllegalStateException("O relatório já foi processado ou falhou.");
        }

        this.saldoPatrimonialInicial = validarValorMonetario(saldoInicial);
        this.saldoPatrimonialFinal = validarValorMonetario(saldoFinal);
        this.totalLucroBrl = validarValorMonetario(lucro);
        this.totalPrejuizoBrl = validarValorMonetario(prejuizo);
        this.volumeTotalNegociado = validarValorMonetario(volume);

        this.urlArquivoGerado = Objects.requireNonNull(urlAWSouGCP, "URL de download é obrigatória no sucesso.");
        this.status = StatusRelatorio.CONCLUIDO;
    }

    public void registrarFalhaProcessamento() {
        this.status = StatusRelatorio.FALHA;
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            if (inicio.isAfter(fim)) {
                throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
            }
            if (fim.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Não é possível gerar relatórios de períodos futuros.");
            }
        }
    }

    private void validarSolicitanteXor(Usuario pf, Empresa pj) {
        if ((pf == null && pj == null) || (pf != null && pj != null)) {
            throw new IllegalArgumentException("O relatório deve ser associado exatamente a uma PF ou uma PJ.");
        }
    }

    private BigDecimal validarValorMonetario(BigDecimal valor) {
        return Objects.requireNonNullElse(valor, BigDecimal.ZERO);
    }

    private void inicializarValoresMonetarios() {
        this.saldoPatrimonialInicial = BigDecimal.ZERO;
        this.saldoPatrimonialFinal = BigDecimal.ZERO;
        this.totalLucroBrl = BigDecimal.ZERO;
        this.totalPrejuizoBrl = BigDecimal.ZERO;
        this.volumeTotalNegociado = BigDecimal.ZERO;
    }

    public enum FormatoArquivo {
        PDF, CSV, EXCEL, JSON_RECEITA_FEDERAL
    }

    public enum StatusRelatorio {
        PROCESSANDO, CONCLUIDO, FALHA
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }

    private void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDate getDataInicioPeriodo() { return dataInicioPeriodo; }
    public void setDataInicioPeriodo(LocalDate dataInicioPeriodo) {
        this.dataInicioPeriodo = dataInicioPeriodo;
        validarPeriodo(this.dataInicioPeriodo, this.dataFimPeriodo);
    }

    public LocalDate getDataFimPeriodo() { return dataFimPeriodo; }
    public void setDataFimPeriodo(LocalDate dataFimPeriodo) {
        this.dataFimPeriodo = dataFimPeriodo;
        validarPeriodo(this.dataInicioPeriodo, this.dataFimPeriodo);
    }

    public BigDecimal getSaldoPatrimonialInicial() { return saldoPatrimonialInicial; }
    public void setSaldoPatrimonialInicial(BigDecimal saldoPatrimonialInicial) {
        this.saldoPatrimonialInicial = validarValorMonetario(saldoPatrimonialInicial);
    }

    public BigDecimal getSaldoPatrimonialFinal() { return saldoPatrimonialFinal; }
    public void setSaldoPatrimonialFinal(BigDecimal saldoPatrimonialFinal) {
        this.saldoPatrimonialFinal = validarValorMonetario(saldoPatrimonialFinal);
    }

    public BigDecimal getTotalLucroBrl() { return totalLucroBrl; }
    public void setTotalLucroBrl(BigDecimal totalLucroBrl) {
        this.totalLucroBrl = validarValorMonetario(totalLucroBrl);
    }

    public BigDecimal getTotalPrejuizoBrl() { return totalPrejuizoBrl; }
    public void setTotalPrejuizoBrl(BigDecimal totalPrejuizoBrl) {
        this.totalPrejuizoBrl = validarValorMonetario(totalPrejuizoBrl);
    }

    public BigDecimal getVolumeTotalNegociado() { return volumeTotalNegociado; }
    public void setVolumeTotalNegociado(BigDecimal volumeTotalNegociado) {
        this.volumeTotalNegociado = validarValorMonetario(volumeTotalNegociado);
    }

    public FormatoArquivo getTipoFormato() { return tipoFormato; }
    public void setTipoFormato(FormatoArquivo tipoFormato) {
        this.tipoFormato = Objects.requireNonNull(tipoFormato);
    }

    public String getUrlArquivoGerado() { return urlArquivoGerado; }
    public void setUrlArquivoGerado(String urlArquivoGerado) {
        this.urlArquivoGerado = urlArquivoGerado;
    }

    public StatusRelatorio getStatus() { return status; }
    public void setStatus(StatusRelatorio status) {
        this.status = Objects.requireNonNull(status);
    }

    public Usuario getSolicitantePessoaFisica() { return solicitantePessoaFisica; }
    public void setSolicitantePessoaFisica(Usuario solicitantePessoaFisica) {
        this.solicitantePessoaFisica = solicitantePessoaFisica;
    }

    public Empresa getSolicitantePessoaJuridica() { return solicitantePessoaJuridica; }
    public void setSolicitantePessoaJuridica(Empresa solicitantePessoaJuridica) {
        this.solicitantePessoaJuridica = solicitantePessoaJuridica;
    }
}