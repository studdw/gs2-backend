package br.com.fiap.rotaparatodos.exception;

// O registro pesquisado nao existe no banco. Vira HTTP 404.
public class EntidadeNaoEncontradaException extends Exception {

    public EntidadeNaoEncontradaException(String msg){
        super(msg);
    }

}
