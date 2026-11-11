package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Entrada para cadastrar ou atualizar um relato (POST /relatos e PUT /relatos/{id}). */
public class RelatoRequestDto {

    @NotNull(message = "Informe o tipo do obstáculo.")
    private TipoObstaculo tipo;

    @NotNull(message = "Informe a gravidade.")
    private Gravidade gravidade;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String descricao;

    @NotBlank(message = "Informe o endereço do local.")
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres.")
    private String endereco;

    @NotBlank(message = "Informe o bairro.")
    @Size(max = 80, message = "O bairro deve ter no máximo 80 caracteres.")
    private String bairro;

    @NotNull(message = "Informe a latitude.")
    @DecimalMin(value = "-90.0", message = "Latitude inválida.")
    @DecimalMax(value = "90.0", message = "Latitude inválida.")
    private Double latitude;

    @NotNull(message = "Informe a longitude.")
    @DecimalMin(value = "-180.0", message = "Longitude inválida.")
    @DecimalMax(value = "180.0", message = "Longitude inválida.")
    private Double longitude;

    // Foto opcional, ja comprimida pelo front, no formato data URL
    @Size(max = 1500000, message = "A foto é grande demais. Envie uma imagem de até 1 MB.")
    @Pattern(regexp = "^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$", message = "A foto deve ser uma imagem JPEG, PNG ou WebP.")
    private String foto;

    public TipoObstaculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoObstaculo tipo) {
        this.tipo = tipo;
    }

    public Gravidade getGravidade() {
        return gravidade;
    }

    public void setGravidade(Gravidade gravidade) {
        this.gravidade = gravidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
