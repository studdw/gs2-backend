package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.StatusRelato;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Entrada da mudanca de status feita pelo gestor (PATCH /relatos/{id}/status). */
public class StatusRelatoRequestDto {

    @NotNull(message = "Informe o novo status.")
    private StatusRelato status;

    @FutureOrPresent(message = "A previsão da obra não pode estar no passado.")
    private LocalDate dataPrevisaoObra;

    public StatusRelato getStatus() {
        return status;
    }

    public void setStatus(StatusRelato status) {
        this.status = status;
    }

    public LocalDate getDataPrevisaoObra() {
        return dataPrevisaoObra;
    }

    public void setDataPrevisaoObra(LocalDate dataPrevisaoObra) {
        this.dataPrevisaoObra = dataPrevisaoObra;
    }
}
