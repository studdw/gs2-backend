package br.com.fiap.rotaparatodos.model;

import java.time.LocalDateTime;

// Mensagem enviada pela pagina Contato (tabela tb_contato). Visitante sem conta tem usuario nulo.
public class Contato {

    private int id;
    private Usuario usuario;
    private String nome;
    private String email;
    private AssuntoContato assunto;
    private String mensagem;
    private StatusContato status;
    private LocalDateTime dataEnvio;

    public Contato() {}

    public Contato(int id, Usuario usuario, String nome, String email, AssuntoContato assunto, String mensagem,
                   StatusContato status, LocalDateTime dataEnvio) {
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.email = email;
        this.assunto = assunto;
        this.mensagem = mensagem;
        this.status = status;
        this.dataEnvio = dataEnvio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
