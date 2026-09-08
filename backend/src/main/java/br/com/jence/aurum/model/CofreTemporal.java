package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class CofreTemporal extends ProdutoAurum {

    private LocalDate dataCriacao;
    private LocalDate dataLiberacao;
    private BigDecimal quantidadeBloqueada;
    private Criptoativo ativoBloqueado;
    private StatusCofre status;
    private Carteira carteiraOrigem;

    public CofreTemporal(Long id, String nomeObjetivo, LocalDate dataLiberacao,
                         BigDecimal quantidade, Criptoativo ativo, Carteira origem) {
        super(id, nomeObjetivo);

        this.dataCriacao = LocalDate.now();
        this.dataLiberacao = Objects.requireNonNull(dataLiberacao);
        this.quantidadeBloqueada = Objects.requireNonNull(quantidade);
        this.ativoBloqueado = ativo;
        this.carteiraOrigem = Objects.requireNonNull(origem);
        this.status = StatusCofre.BLOQUEADO;

        validarDisponibilidade();
    }

    @Override
    public void validarDisponibilidade() {
        if (!dataLiberacao.isAfter(dataCriacao)) {
            this.disponivel = false; // Flag herdada do ProdutoAurum
            throw new IllegalArgumentException("O cofre exige um bloqueio mínimo de pelo menos 1 dia para o futuro.");
        }
        this.disponivel = true;
    }

    public boolean isLiberadoParaResgate() {
        return !LocalDate.now().isBefore(dataLiberacao);
    }

    public void processarResgate() {
        if (!isLiberadoParaResgate()) {
            throw new IllegalStateException("O ativo ainda está em período de carência.");
        }
        if (this.status == StatusCofre.RESGATADO) {
            throw new IllegalStateException("O cofre já foi resgatado anteriormente.");
        }

        this.status = StatusCofre.RESGATADO;
    }

    public enum StatusCofre {
        BLOQUEADO,
        RESGATADO,
        CANCELADO
    }

    public LocalDate getDataCriacao() { return dataCriacao; }

    private void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataLiberacao() { return dataLiberacao; }
    public void setDataLiberacao(LocalDate dataLiberacao) {
        this.dataLiberacao = Objects.requireNonNull(dataLiberacao);
        validarDisponibilidade(); // Blindagem: Se alguém alterar a data por setter, revalida a regra!
    }

    public BigDecimal getQuantidadeBloqueada() { return quantidadeBloqueada; }
    public void setQuantidadeBloqueada(BigDecimal quantidadeBloqueada) {
        if (quantidadeBloqueada == null || quantidadeBloqueada.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade bloqueada deve ser maior que zero.");
        }
        this.quantidadeBloqueada = quantidadeBloqueada;
    }

    public Criptoativo getAtivoBloqueado() { return ativoBloqueado; }
    public void setAtivoBloqueado(Criptoativo ativoBloqueado) {
        this.ativoBloqueado = ativoBloqueado;
    }

    public StatusCofre getStatus() { return status; }
    public void setStatus(StatusCofre status) {
        this.status = Objects.requireNonNull(status);
    }

    public Carteira getCarteiraOrigem() { return carteiraOrigem; }
    public void setCarteiraOrigem(Carteira carteiraOrigem) {
        this.carteiraOrigem = Objects.requireNonNull(carteiraOrigem);
    }
}