package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.Papel;
import br.com.fiap.rotaparatodos.model.PerfilMobilidade;
import java.time.LocalDateTime;

/** Saida com os dados do usuario. A senha nunca e devolvida. */
public class UsuarioResponseDto {

    private int id;

    private String nome;

    private String email;

    private Papel papel;

    private PerfilMobilidade perfilMobilidade;

    private LocalDateTime dataCadastro;

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
