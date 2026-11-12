package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.AssuntoContato;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada do formulario de Contato (POST /contatos). Mesmas regras da validacao do front. */
public class ContatoRequestDto {

    @NotBlank(message = "Informe o seu nome.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    @NotNull(message = "Escolha um assunto.")
    private AssuntoContato assunto;

    @NotBlank(message = "Escreva a mensagem.")
    @Size(min = 10, max = 1000, message = "A mensagem deve ter entre 10 e 1000 caracteres.")
    private String mensagem;

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
}
