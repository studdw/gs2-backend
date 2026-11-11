package br.com.fiap.rotaparatodos.model;

import java.time.LocalDateTime;

// Voto de um cidadao sobre um relato: validacao comunitaria (tabela tb_validacao)
public class Validacao {

    private int id;
    private Relato relato;
    private Usuario usuario;
    private TipoVoto voto;
    private String comentario;
    private LocalDateTime dataValidacao;

    public Validacao() {}

    public Validacao(int id, Relato relato, Usuario usuario, TipoVoto voto, String comentario,
                     LocalDateTime dataValidacao) {
        this.id = id;
        this.relato = relato;
        this.usuario = usuario;
        this.voto = voto;
        this.comentario = comentario;
        this.dataValidacao = dataValidacao;
    }

    public boolean pertenceA(Usuario outro) {
        return outro != null && usuario != null && usuario.getId() == outro.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Relato getRelato() {
        return relato;
    }

    public void setRelato(Relato relato) {
        this.relato = relato;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoVoto getVoto() {
        return voto;
    }

    public void setVoto(TipoVoto voto) {
        this.voto = voto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(LocalDateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }
}
