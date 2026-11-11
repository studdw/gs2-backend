package br.com.fiap.rotaparatodos.exception;

// Ja existe uma conta com o e-mail informado.
public class EmailJaCadastradoException extends RegraNegocioException {

    public EmailJaCadastradoException(){
        super("Já existe uma conta com este e-mail. Tente entrar ou use outro e-mail.");
    }

}
