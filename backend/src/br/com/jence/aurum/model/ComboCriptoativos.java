package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ComboCriptoativos {
    private final Long id;
    private String nome;
    private String descricao;
    private PerfilRisco perfilRisco;
    private final List<ItemCombo> composicao;
    private boolean disponivelParaCompra;

    public ComboCriptoativos(Long id, String nome, PerfilRisco perfilRisco) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.perfilRisco = Objects.requireNonNull(perfilRisco);
        this.composicao = new ArrayList<>();
        this.disponivelParaCompra = false;
    }

    public void adicionarItem(ItemCombo item) {
        Objects.requireNonNull(item, "Item do combo não pode ser nulo.");
        this.composicao.add(item);
        validarDisponibilidade();
    }

    public List<ItemCombo> getComposicao() {
        return Collections.unmodifiableList(composicao);
    }

    public BigDecimal calcularTotalAlocacao() {
        return composicao.stream()
                .map(ItemCombo::getPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarDisponibilidade() {
        BigDecimal total = calcularTotalAlocacao();
        boolean composicaoIntegra = total.compareTo(new BigDecimal("100.00")) == 0;
        this.disponivelParaCompra = !composicao.isEmpty() && composicaoIntegra;
    }

    public void setDisponivelParaCompra(boolean disponivel) {
        if (disponivel) {
            validarDisponibilidade();
            if (!this.disponivelParaCompra) {
                throw new IllegalStateException("Combo incompleto ou alocação diferente de 100%.");
            }
        }
        this.disponivelParaCompra = disponivel;
    }

    public enum PerfilRisco {
        BAIXO, MEDIO, ALTO
    }
}