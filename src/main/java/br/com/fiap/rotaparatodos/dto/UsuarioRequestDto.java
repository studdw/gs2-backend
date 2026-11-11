package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.PerfilMobilidade;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada do cadastro de usuario (POST /usuarios). */
public class UsuarioRequestDto {

    @NotBlank(message = "Informe o seu nome.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    @NotBlank(message = "Crie uma senha.")
    @Size(min = 8, max = 60, message = "A senha deve ter entre 8 e 60 caracteres.")
    private String senha;

    private PerfilMobilidade perfilMobilidade;

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

    public PerfilMobilidade getPerfilMobilidade() {
        return perfilMobilidade;
    }

    public void setPerfilMobilidade(PerfilMobilidade perfilMobilidade) {
        this.perfilMobilidade = perfilMobilidade;
    }
}
