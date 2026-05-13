package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class CofreTemporal {
    private final Long id;
    private final String nomeObjetivo;
    private final LocalDate dataCriacao;
    private final LocalDate dataLiberacao;
    private final BigDecimal quantidadeBloqueada;
    private final Criptoativo ativoBloqueado;
    private StatusCofre status;
    private final Carteira carteiraOrigem;

    public CofreTemporal(Long id, String nomeObjetivo, LocalDate dataLiberacao,
                         BigDecimal quantidade, Criptoativo ativo, Carteira origem) {
        this.id = Objects.requireNonNull(id);
        this.nomeObjetivo = nomeObjetivo;
        this.dataCriacao = LocalDate.now();
        this.dataLiberacao = Objects.requireNonNull(dataLiberacao);
        this.quantidadeBloqueada = Objects.requireNonNull(quantidade);
        this.ativoBloqueado = ativo;
        this.carteiraOrigem = Objects.requireNonNull(origem);
        this.status = StatusCofre.BLOQUEADO;

        validarDataLiberacao();
    }

    private void validarDataLiberacao() {
        if (!dataLiberacao.isAfter(dataCriacao)) {
            throw new IllegalArgumentException("O cofre exige um bloqueio mínimo de pelo menos 1 dia para o futuro.");
        }
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
}