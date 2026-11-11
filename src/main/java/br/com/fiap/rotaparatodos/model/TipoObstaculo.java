package br.com.fiap.rotaparatodos.model;

/** Tipos de obstaculo que podem ser reportados em uma calcada (HU06). */
public enum TipoObstaculo {
    BURACO("Buraco"),
    DEGRAU("Degrau"),
    SEM_RAMPA("Falta de rampa"),
    CALCADA_IRREGULAR("Calçada irregular"),
    OBSTRUCAO("Obstrução na calçada"),
    ILUMINACAO_RUIM("Iluminação ruim"),
    LOCAL_ISOLADO("Local isolado"),
    TRAVESSIA_INSEGURA("Travessia sem semáforo sonoro");

    private final String rotulo;

    TipoObstaculo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
