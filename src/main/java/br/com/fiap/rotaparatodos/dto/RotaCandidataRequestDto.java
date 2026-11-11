package br.com.fiap.rotaparatodos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Um trajeto candidato: distancia total e os pontos [latitude, longitude]. */
public class RotaCandidataRequestDto {

    @NotNull(message = "Informe a distância da rota.")
    @Positive(message = "A distância da rota deve ser maior que zero.")
    private Double distanciaMetros;

    @NotNull(message = "Informe os pontos da rota.")
    @Size(min = 2, max = 5000, message = "A rota deve ter entre 2 e 5000 pontos.")
    private List<double[]> pontos;

    public Double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(Double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public List<double[]> getPontos() {
        return pontos;
    }

    public void setPontos(List<double[]> pontos) {
        this.pontos = pontos;
    }
}
