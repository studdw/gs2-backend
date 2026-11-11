package br.com.fiap.rotaparatodos.dto;

/** Item do ranking de pontos criticos (HU11). */
public class PontoCriticoResponseDto {

    private int posicao;

    private RelatoResponseDto relato;

    private int indiceImpacto;

    private ComposicaoImpactoResponseDto composicao;

    private String equipamentoProximo;

    private int distanciaEquipamentoMetros;

    public PontoCriticoResponseDto() {}

    public PontoCriticoResponseDto(int posicao, RelatoResponseDto relato, int indiceImpacto, ComposicaoImpactoResponseDto composicao, String equipamentoProximo, int distanciaEquipamentoMetros) {
        this.posicao = posicao;
        this.relato = relato;
        this.indiceImpacto = indiceImpacto;
        this.composicao = composicao;
        this.equipamentoProximo = equipamentoProximo;
        this.distanciaEquipamentoMetros = distanciaEquipamentoMetros;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public RelatoResponseDto getRelato() {
        return relato;
    }

    public void setRelato(RelatoResponseDto relato) {
        this.relato = relato;
    }

    public int getIndiceImpacto() {
        return indiceImpacto;
    }

    public void setIndiceImpacto(int indiceImpacto) {
        this.indiceImpacto = indiceImpacto;
    }

    public ComposicaoImpactoResponseDto getComposicao() {
        return composicao;
    }

    public void setComposicao(ComposicaoImpactoResponseDto composicao) {
        this.composicao = composicao;
    }

    public String getEquipamentoProximo() {
        return equipamentoProximo;
    }

    public void setEquipamentoProximo(String equipamentoProximo) {
        this.equipamentoProximo = equipamentoProximo;
    }

    public int getDistanciaEquipamentoMetros() {
        return distanciaEquipamentoMetros;
    }

    public void setDistanciaEquipamentoMetros(int distanciaEquipamentoMetros) {
        this.distanciaEquipamentoMetros = distanciaEquipamentoMetros;
    }
}
