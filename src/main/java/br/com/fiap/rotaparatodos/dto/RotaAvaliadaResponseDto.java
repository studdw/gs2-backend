package br.com.fiap.rotaparatodos.dto;

import java.util.List;

/** Uma rota com o indice de acessibilidade (0 a 100) e os alertas. */
public class RotaAvaliadaResponseDto {

    private int posicaoOriginal;

    private int distanciaMetros;

    private int tempoMinutos;

    private int indiceAcessibilidade;

    private String classificacao;

    private boolean bloqueada;

    private List<AlertaRotaResponseDto> alertas;

    public RotaAvaliadaResponseDto() {}

    public RotaAvaliadaResponseDto(int posicaoOriginal, int distanciaMetros, int tempoMinutos, int indiceAcessibilidade, String classificacao, boolean bloqueada, List<AlertaRotaResponseDto> alertas) {
        this.posicaoOriginal = posicaoOriginal;
        this.distanciaMetros = distanciaMetros;
        this.tempoMinutos = tempoMinutos;
        this.indiceAcessibilidade = indiceAcessibilidade;
        this.classificacao = classificacao;
        this.bloqueada = bloqueada;
        this.alertas = alertas;
    }

    public int getPosicaoOriginal() {
        return posicaoOriginal;
    }

    public void setPosicaoOriginal(int posicaoOriginal) {
        this.posicaoOriginal = posicaoOriginal;
    }

    public int getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(int distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public int getTempoMinutos() {
        return tempoMinutos;
    }

    public void setTempoMinutos(int tempoMinutos) {
        this.tempoMinutos = tempoMinutos;
    }

    public int getIndiceAcessibilidade() {
        return indiceAcessibilidade;
    }

    public void setIndiceAcessibilidade(int indiceAcessibilidade) {
        this.indiceAcessibilidade = indiceAcessibilidade;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
    }

    public List<AlertaRotaResponseDto> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<AlertaRotaResponseDto> alertas) {
        this.alertas = alertas;
    }
}
