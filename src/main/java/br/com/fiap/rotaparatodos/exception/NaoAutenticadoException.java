package br.com.fiap.rotaparatodos.exception;

// A requisicao precisa de um usuario logado (token ausente, invalido ou vencido). Vira HTTP 401.
public class NaoAutenticadoException extends Exception {

    public NaoAutenticadoException(String msg){
        super(msg);
    }

}
