package br.com.fiap.rotaparatodos.exception;

// Tentativa de remover um registro que ainda tem filhos (ex.: relato com validacoes).
public class RegistroComDependenciasException extends RegraNegocioException {

    public RegistroComDependenciasException(String msg){
        super(msg);
    }

}
