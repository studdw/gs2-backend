package br.com.fiap.rotaparatodos.exception;

// RF12 - ja existe um relato ativo do mesmo tipo a menos de 15 metros.
public class RelatoDuplicadoException extends RegraNegocioException {

    public RelatoDuplicadoException(String protocolo){
        super("Já existe um relato deste tipo a menos de 15 metros (protocolo " + protocolo
                + "). Confirme o relato existente em vez de criar outro.");
    }

}
