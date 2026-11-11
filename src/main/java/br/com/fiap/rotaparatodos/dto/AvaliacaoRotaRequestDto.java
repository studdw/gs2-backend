package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.PerfilMobilidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Entrada da avaliacao de rotas (POST /rotas/avaliacoes). */
public class AvaliacaoRotaRequestDto {

    @NotNull(message = "Informe o perfil de mobilidade.")
    private PerfilMobilidade perfil;

    // Hora do trajeto (0 a 23). Se nao vier, a API usa a hora atual.
    @Min(value = 0, message = "A hora deve estar entre 0 e 23.")
    @Max(value = 23, message = "A hora deve estar entre 0 e 23.")
    private Integer hora;

    @NotEmpty(message = "Envie ao menos uma rota para avaliar.")
    @Size(max = 5, message = "Envie no máximo 5 rotas por vez.")
    @Valid
    private List<RotaCandidataRequestDto> rotas;

    public PerfilMobilidade getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilMobilidade perfil) {
        this.perfil = perfil;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public List<RotaCandidataRequestDto> getRotas() {
        return rotas;
    }

    public void setRotas(List<RotaCandidataRequestDto> rotas) {
        this.rotas = rotas;
    }
}
