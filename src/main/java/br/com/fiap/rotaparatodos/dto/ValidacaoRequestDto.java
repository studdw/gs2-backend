package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.TipoVoto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada de um voto (POST /relatos/{id}/validacoes). A localizacao e obrigatoria (RN14). */
public class ValidacaoRequestDto {

    @NotNull(message = "Escolha uma opção de voto.")
    private TipoVoto voto;

    @Size(max = 200, message = "O comentário deve ter no máximo 200 caracteres.")
    private String comentario;

    @NotNull(message = "Informe a latitude.")
    @DecimalMin(value = "-90.0", message = "Latitude inválida.")
    @DecimalMax(value = "90.0", message = "Latitude inválida.")
    private Double latitude;

    @NotNull(message = "Informe a longitude.")
    @DecimalMin(value = "-180.0", message = "Longitude inválida.")
    @DecimalMax(value = "180.0", message = "Longitude inválida.")
    private Double longitude;

    public TipoVoto getVoto() {
        return voto;
    }

    public void setVoto(TipoVoto voto) {
        this.voto = voto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
