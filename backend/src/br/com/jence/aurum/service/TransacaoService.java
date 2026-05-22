package br.com.jence.aurum.service;

import br.com.jence.aurum.model.Carteira;
import br.com.jence.aurum.model.Criptoativo;
import br.com.jence.aurum.model.Transacao;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
}
