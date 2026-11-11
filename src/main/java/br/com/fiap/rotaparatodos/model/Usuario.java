package br.com.fiap.rotaparatodos.model;

import java.time.LocalDateTime;

// Pessoa que usa a plataforma: cidadao ou gestor publico (tabela tb_usuario)
public class Usuario {

    private int id;
    private String nome;
    private String email;
    // Hash BCrypt da senha. A senha em texto puro nunca e guardada.
    private String senha;
    private Papel papel;
    private PerfilMobilidade perfilMobilidade;
    private LocalDateTime dataCadastro;

    public Usuario() {}

    public Usuario(int id) {
        this.id = id;
    }

    public Usuario(int id, String nome, String email, String senha, Papel papel,
                   PerfilMobilidade perfilMobilidade, LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.papel = papel;
        this.perfilMobilidade = perfilMobilidade;
        this.dataCadastro = dataCadastro;
    }

    public boolean isGestor() {
        return papel == Papel.GESTOR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public PerfilMobilidade getPerfilMobilidade() {
        return perfilMobilidade;
    }

    public void setPerfilMobilidade(PerfilMobilidade perfilMobilidade) {
        this.perfilMobilidade = perfilMobilidade;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
