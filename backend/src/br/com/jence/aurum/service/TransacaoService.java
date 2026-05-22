package br.com.jence.aurum.service;

import br.com.jence.aurum.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TransacaoService {

    public Transacao realizarCompra(Carteira carteira, Criptoativo criptoativo, BigDecimal valorBrl) {
        if (carteira.getSaldoDisponivelBrl().compareTo(valorBrl) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        BigDecimal precoAtual = criptoativo.getPrecoAtualBrl();
        BigDecimal quantidade = valorBrl.divide(precoAtual, 8, RoundingMode.HALF_UP);

        carteira.sacarBrl(valorBrl);

        carteira.atualizarPosicaoCripto(
                criptoativo,
                quantidade,
                true
        );

        Transacao transacao = new Transacao(
                1L,
                Transacao.TipoTransacao.COMPRA,
                valorBrl,
                quantidade,
                precoAtual,
                new BigDecimal("2"),
                carteira,
                criptoativo
        );

        transacao.marcarComoConcluida();

        carteira.registrarTransacao(transacao);

        return transacao;
    }

    public Transacao realizarVenda(Carteira carteira, Criptoativo criptoativo, BigDecimal valorBrl) {
        BigDecimal precoAtual = criptoativo.getPrecoAtualBrl();
        BigDecimal quantidade = valorBrl.divide(precoAtual, 8, RoundingMode.HALF_UP);

        carteira.atualizarPosicaoCripto(
                criptoativo,
                quantidade,
                false
        );

        BigDecimal valorVenda = quantidade.multiply(precoAtual);

        carteira.depositarBrl(valorVenda);

        Transacao transacao = new Transacao(
                2L,
                Transacao.TipoTransacao.VENDA,
                valorVenda,
                quantidade,
                precoAtual,
                new BigDecimal("2"),
                carteira,
                criptoativo
        );

        transacao.marcarComoConcluida();

        carteira.registrarTransacao(transacao);

        return transacao;
    }

    public List<Transacao> realizarCompraCombo(Carteira carteira, ComboCriptoativos comboCriptoativos, BigDecimal valorBrl) {

        if (!comboCriptoativos.isDisponivel()) {
            throw new IllegalStateException("Combo indisponível");
        }

        if (carteira.getSaldoDisponivelBrl().compareTo(valorBrl) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        List<Transacao> transacaoList = new ArrayList<>();

        for (ItemCombo item : comboCriptoativos.getComposicao()) {
            BigDecimal percentual = item.getPercentual();

            BigDecimal valorItem = valorBrl
                    .multiply(percentual)
                    .divide(
                            new BigDecimal("100"),
                            8,
                            RoundingMode.HALF_UP
                    );

            transacaoList.add(realizarCompra(carteira, item.getMoeda(), valorItem));
        }

        return  transacaoList;
    }
}
