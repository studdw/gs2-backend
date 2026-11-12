package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.StatusContato;
import jakarta.validation.constraints.NotNull;

/** Entrada para o gestor mudar a situacao de uma mensagem (PATCH /contatos/{id}/status). */
public class StatusContatoRequestDto {

    @NotNull(message = "Informe o novo status.")
    private StatusContato status;

    public StatusContato getStatus() {
        return status;
    }

    public void setStatus(StatusContato status) {
        this.status = status;
    }
}
