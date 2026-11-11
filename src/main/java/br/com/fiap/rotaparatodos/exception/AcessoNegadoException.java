package br.com.fiap.rotaparatodos.exception;

// O usuario esta logado, mas nao tem permissao para a operacao. Vira HTTP 403.
public class AcessoNegadoException extends Exception {

    public AcessoNegadoException(String msg){
        super(msg);
    }

}
