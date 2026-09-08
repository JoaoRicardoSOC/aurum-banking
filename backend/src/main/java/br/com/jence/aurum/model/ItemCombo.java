package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemCombo {
    private final Long id;
    private final Criptoativo moeda;
    private final BigDecimal percentual;

    public ItemCombo(Long id, Criptoativo moeda, BigDecimal percentual) {
        this.id = Objects.requireNonNull(id);
        this.moeda = Objects.requireNonNull(moeda);
        if (percentual == null || percentual.compareTo(BigDecimal.ZERO) <= 0 || percentual.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentual deve ser entre 0 e 100.");
        }
        this.percentual = percentual;
    }

    public Long getId() { return id; }
    public Criptoativo getMoeda() { return moeda; }
    public BigDecimal getPercentual() { return percentual; }
}