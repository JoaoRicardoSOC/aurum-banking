package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class RelatorioFiscal {
    private final Long id;
    private final LocalDateTime dataSolicitacao;
    private final LocalDate dataInicioPeriodo;
    private final LocalDate dataFimPeriodo;

    private BigDecimal saldoPatrimonialInicial;
    private BigDecimal saldoPatrimonialFinal;
    private BigDecimal totalLucroBrl;
    private BigDecimal totalPrejuizoBrl;
    private BigDecimal volumeTotalNegociado;

    private final FormatoArquivo tipoFormato;
    private String urlArquivoGerado;
    private StatusRelatorio status;

    private final Usuario solicitantePessoaFisica;
    private final Empresa solicitantePessoaJuridica;

    public RelatorioFiscal(Long id, LocalDate inicio, LocalDate fim, FormatoArquivo formato,
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
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }
        if (fim.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível gerar relatórios de períodos futuros.");
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

    public StatusRelatorio getStatus() {
        return this.status;
    }

    public Long getId() {
        return this.id;
    }

    public String getUrlArquivoGerado() {
        return this.urlArquivoGerado;
    }
}