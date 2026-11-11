package br.com.fiap.rotaparatodos.exception;

// RN10 - o usuario ja enviou o maximo de relatos permitido em 24 horas.
public class LimiteRelatosException extends RegraNegocioException {

    public LimiteRelatosException(int limite){
        super("Você já enviou " + limite + " relatos hoje. Obrigado! Volte amanhã para enviar mais.");
    }

}
