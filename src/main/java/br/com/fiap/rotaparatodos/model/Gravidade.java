package br.com.fiap.rotaparatodos.model;

/**
 * Gravidade do obstaculo.
 * "pontos" (0 a 100) entra no Indice de Impacto; "fator" multiplica a penalidade na rota.
 */
public enum Gravidade {
    BAIXA("Baixa", 25, 0.5),
    MEDIA("Média", 50, 1.0),
    ALTA("Alta", 75, 1.5),
    CRITICA("Crítica", 100, 2.0);

    private final String rotulo;
    private final int pontos;
    private final double fator;

    Gravidade(String rotulo, int pontos, double fator) {
        this.rotulo = rotulo;
        this.pontos = pontos;
        this.fator = fator;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getPontos() {
        return pontos;
    }

    public double getFator() {
        return fator;
    }
}
