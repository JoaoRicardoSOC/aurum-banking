package br.com.jence.aurum.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notificacao implements RegistroAuditavel {

    private Long id;
    private String titulo;
    private String corpoMensagem;
    private LocalDateTime dataHoraEnvio;
    private PublicoAlvo publicoAlvo;
    private TipoAlerta tipoAlerta;
    private boolean lida;
    private String linkAcao;
    private Usuario destinatario;

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

    @Override
    public LocalDateTime getDataHoraRegistro() {
        return this.dataHoraEnvio;
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
    public void setId(Long id) { this.id = Objects.requireNonNull(id); }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        this.titulo = validarTexto(titulo, "Título");
    }

    public String getCorpoMensagem() { return corpoMensagem; }
    public void setCorpoMensagem(String mensagem) {
        this.corpoMensagem = validarTexto(mensagem, "Corpo da mensagem");
    }

    public LocalDateTime getDataHoraEnvio() { return dataHoraEnvio; }

    private void setDataHoraEnvio(LocalDateTime dataHoraEnvio) {
        this.dataHoraEnvio = dataHoraEnvio;
    }

    public PublicoAlvo getPublicoAlvo() { return publicoAlvo; }
    public void setPublicoAlvo(PublicoAlvo publicoAlvo) {
        this.publicoAlvo = Objects.requireNonNull(publicoAlvo);
    }

    public TipoAlerta getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(TipoAlerta tipoAlerta) {
        this.tipoAlerta = Objects.requireNonNull(tipoAlerta);
    }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public String getLinkAcao() { return linkAcao; }
    public void setLinkAcao(String linkAcao) { this.linkAcao = linkAcao; }

    public Usuario getDestinatario() { return destinatario; }
    public void setDestinatario(Usuario destinatario) {
        this.destinatario = Objects.requireNonNull(destinatario);
    }

    public enum TipoAlerta {
        INFO, SUCESSO, AVISO, CRITICO, SEGURANCA
    }

    public enum PublicoAlvo {
        INICIANTE, AVANCADO, TODOS
    }
}