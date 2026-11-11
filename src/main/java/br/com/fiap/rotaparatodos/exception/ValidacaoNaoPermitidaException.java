package br.com.fiap.rotaparatodos.exception;

// RN14/RN16 - o voto nao pode ser registrado (autor do relato, longe do local, voto repetido...).
public class ValidacaoNaoPermitidaException extends RegraNegocioException {

    public ValidacaoNaoPermitidaException(String msg){
        super(msg);
    }

}
