package br.com.fiap.rotaparatodos.exception;

// Antispam do Contato: o mesmo e-mail ja mandou o maximo de mensagens permitido em 1 hora.
public class LimiteContatosException extends RegraNegocioException {

    public LimiteContatosException(int limite){
        super("Você já enviou " + limite + " mensagens na última hora. Aguarde um pouco para escrever de novo.");
    }

}
