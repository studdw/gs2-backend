package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.AssuntoContato;
import br.com.fiap.rotaparatodos.model.StatusContato;
import java.time.LocalDateTime;

/** Saida com os dados de uma mensagem de contato. usuarioId e nulo quando quem escreveu nao tinha conta. */
public class ContatoResponseDto {

    private int id;

    private Integer usuarioId;

    private String nome;

    private String email;

    private AssuntoContato assunto;

    private String mensagem;

    private StatusContato status;

    private LocalDateTime dataEnvio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AssuntoContato getAssunto() {
        return assunto;
    }

    public void setAssunto(AssuntoContato assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public StatusContato getStatus() {
        return status;
    }

    public void setStatus(StatusContato status) {
        this.status = status;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
