package br.com.jence.aurum.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ComboCriptoativos extends ProdutoAurum {

    private String descricao;
    private PerfilRisco perfilRisco;
    private List<ItemCombo> composicao;

    public ComboCriptoativos(Long id, String nome, PerfilRisco perfilRisco) {
        super(id, nome);
        this.perfilRisco = Objects.requireNonNull(perfilRisco, "O perfil de risco é obrigatório.");
        this.composicao = new ArrayList<>();
    }

    @Override
    public void validarDisponibilidade() {
        if (this.composicao == null || this.composicao.isEmpty()) {
            this.disponivel = false;
            return;
        }

        BigDecimal total = calcularTotalAlocacao();

        this.disponivel = total.compareTo(new BigDecimal("100.00")) == 0;
    }

    @Override
    public void setDisponivel(boolean disponivel) {
        if (disponivel) {
            validarDisponibilidade();
            if (!this.disponivel) {
                throw new IllegalStateException("SEGURANÇA: O Combo não pode ser ativado para venda pois a alocação não soma 100.00%.");
            }
        } else {
            this.disponivel = false;
        }
    }

    public void adicionarItem(ItemCombo item) {
        Objects.requireNonNull(item, "Item do combo não pode ser nulo.");
        this.composicao.add(item);
        validarDisponibilidade();
    }

    public BigDecimal calcularTotalAlocacao() {
        return composicao.stream()
                .map(ItemCombo::getPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public PerfilRisco getPerfilRisco() { return perfilRisco; }
    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = Objects.requireNonNull(perfilRisco);
    }

    public List<ItemCombo> getComposicao() {
        return Collections.unmodifiableList(composicao);
    }

    public void setComposicao(List<ItemCombo> composicao) {
        this.composicao = new ArrayList<>(Objects.requireNonNull(composicao));
        validarDisponibilidade();
    }

    public enum PerfilRisco {
        BAIXO, MEDIO, ALTO
    }
}