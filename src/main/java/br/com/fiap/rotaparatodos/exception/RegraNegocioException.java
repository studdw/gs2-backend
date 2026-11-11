package br.com.fiap.rotaparatodos.exception;

// Uma regra do dominio foi violada. Vira HTTP 409.
// As excecoes das regras especificas sao filhas desta classe.
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String msg){
        super(msg);
    }

}
