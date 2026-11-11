package br.com.fiap.rotaparatodos.model;

/**
 * Perfil de mobilidade do cidadao (HU02).
 * Cada perfil tem a velocidade media de caminhada (m/s), usada para estimar o tempo da rota,
 * e a matriz de pesos dos obstaculos (RN02): quanto cada tipo de obstaculo atrapalha esse perfil.
 */
public enum PerfilMobilidade {

    //                   velocidade, buraco, degrau, s/rampa, irregular, obstrucao, iluminacao, isolado, travessia
    CADEIRANTE(1.0, 18, 30, 30, 15, 20, 6, 6, 8),
    IDOSO(0.9, 15, 14, 6, 14, 8, 10, 10, 10),
    GESTANTE(1.0, 10, 8, 4, 10, 8, 8, 10, 8),
    CARRINHO_BEBE(1.0, 14, 24, 24, 12, 16, 6, 8, 8),
    // RN08 - para deficiencia visual, travessia sem semaforo sonoro tem penalidade alta
    DEFICIENCIA_VISUAL(0.9, 18, 14, 4, 14, 18, 6, 8, 25),
    MULETA_ANDADOR(0.7, 16, 20, 10, 16, 12, 8, 8, 10);

    private final double velocidadeMetrosPorSegundo;
    // Pesos na mesma ordem de TipoObstaculo.values()
    private final int[] pesos;

    PerfilMobilidade(double velocidadeMetrosPorSegundo, int... pesos) {
        this.velocidadeMetrosPorSegundo = velocidadeMetrosPorSegundo;
        this.pesos = pesos;
    }

    public double getVelocidadeMetrosPorSegundo() {
        return velocidadeMetrosPorSegundo;
    }

    // Peso do obstaculo para este perfil. ordinal() e a posicao do tipo dentro do enum TipoObstaculo.
    public int pesoDe(TipoObstaculo tipo) {
        return pesos[tipo.ordinal()];
    }
}
