package br.com.jence.aurum.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notificacao {
    private final Long id;
    private final String titulo;
    private final String corpoMensagem;
    private final LocalDateTime dataHoraEnvio;
    private final PublicoAlvo publicoAlvo;
    private final TipoAlerta tipoAlerta;
    private boolean lida;
    private final String linkAcao;
    private final Usuario destinatario;

    public Notificacao(Long id, String titulo, String mensagem, TipoAlerta tipo, Usuario destinatario) {
        this(id, titulo, mensagem, tipo, destinatario, null, PublicoAlvo.TODOS);
    }

    public Notificacao(Long id, String titulo, String mensagem, TipoAlerta tipo,
                       Usuario destinatario, String linkAcao, PublicoAlvo publico) {
        this.id = Objects.requireNonNull(id);
        this.titulo = validarTexto(titulo, "Título");
        this.corpoMensagem = validarTexto(mensagem, "Corpo da mensagem");
        this.tipoAlerta = Objects.requireNonNull(tipo);
        this.destinatario = Objects.requireNonNull(destinatario);
        this.linkAcao = linkAcao;
        this.publicoAlvo = Objects.requireNonNull(publico);
        this.dataHoraEnvio = LocalDateTime.now();
        this.lida = false;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public boolean possuiAcao() {
        return linkAcao != null && !linkAcao.isBlank();
    }

    private String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
        return texto.trim();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getCorpoMensagem() { return corpoMensagem; }
    public LocalDateTime getDataHoraEnvio() { return dataHoraEnvio; }
    public boolean isLida() { return lida; }
    public String getLinkAcao() { return linkAcao; }
    public Usuario getDestinatario() { return destinatario; }
    public TipoAlerta getTipoAlerta() { return tipoAlerta; }
    public PublicoAlvo getPublicoAlvo() { return publicoAlvo; }

    public enum TipoAlerta {
        INFO, SUCESSO, AVISO, CRITICO, SEGURANCA
    }

    public enum PublicoAlvo {
        INICIANTE, AVANCADO, TODOS
    }
}