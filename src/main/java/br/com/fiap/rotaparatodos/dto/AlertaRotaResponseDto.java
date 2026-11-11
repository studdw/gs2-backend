package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.StatusRelato;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;

/** Ponto de atencao do trajeto: um relato ativo perto do caminho (RF08). */
public class AlertaRotaResponseDto {

    private int relatoId;

    private TipoObstaculo tipo;

    private Gravidade gravidade;

    private StatusRelato status;

    private String endereco;

    private double latitude;

    private double longitude;

    private int distanciaDaOrigemMetros;

    private int penalidade;

    private boolean bloqueia;

    public AlertaRotaResponseDto() {}

    public AlertaRotaResponseDto(int relatoId, TipoObstaculo tipo, Gravidade gravidade, StatusRelato status, String endereco, double latitude, double longitude, int distanciaDaOrigemMetros, int penalidade, boolean bloqueia) {
        this.relatoId = relatoId;
        this.tipo = tipo;
        this.gravidade = gravidade;
        this.status = status;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanciaDaOrigemMetros = distanciaDaOrigemMetros;
        this.penalidade = penalidade;
        this.bloqueia = bloqueia;
    }

    public int getRelatoId() {
        return relatoId;
    }

    public void setRelatoId(int relatoId) {
        this.relatoId = relatoId;
    }

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

    public StatusRelato getStatus() {
        return status;
    }

    public void setStatus(StatusRelato status) {
        this.status = status;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDistanciaDaOrigemMetros() {
        return distanciaDaOrigemMetros;
    }

    public void setDistanciaDaOrigemMetros(int distanciaDaOrigemMetros) {
        this.distanciaDaOrigemMetros = distanciaDaOrigemMetros;
    }

    public int getPenalidade() {
        return penalidade;
    }

    public void setPenalidade(int penalidade) {
        this.penalidade = penalidade;
    }

    public boolean isBloqueia() {
        return bloqueia;
    }

    public void setBloqueia(boolean bloqueia) {
        this.bloqueia = bloqueia;
    }
}
