package br.com.fiap.rotaparatodos.dto;

/** Fatores (0 a 100) do Indice de Impacto: 0,40 G + 0,25 C + 0,20 P + 0,15 F (RN19). */
public class ComposicaoImpactoResponseDto {

    private int gravidade;

    private int confirmacoes;

    private int proximidade;

    private int fluxoPedestres;

    public ComposicaoImpactoResponseDto() {}

    public ComposicaoImpactoResponseDto(int gravidade, int confirmacoes, int proximidade, int fluxoPedestres) {
        this.gravidade = gravidade;
        this.confirmacoes = confirmacoes;
        this.proximidade = proximidade;
        this.fluxoPedestres = fluxoPedestres;
    }

    public int getGravidade() {
        return gravidade;
    }

    public void setGravidade(int gravidade) {
        this.gravidade = gravidade;
    }

    public int getConfirmacoes() {
        return confirmacoes;
    }

    public void setConfirmacoes(int confirmacoes) {
        this.confirmacoes = confirmacoes;
    }

    public int getProximidade() {
        return proximidade;
    }

    public void setProximidade(int proximidade) {
        this.proximidade = proximidade;
    }

    public int getFluxoPedestres() {
        return fluxoPedestres;
    }

    public void setFluxoPedestres(int fluxoPedestres) {
        this.fluxoPedestres = fluxoPedestres;
    }
}
