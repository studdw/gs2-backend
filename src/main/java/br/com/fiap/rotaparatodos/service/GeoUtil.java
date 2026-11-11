package br.com.fiap.rotaparatodos.service;

import java.util.List;

// Calculos geograficos simples (distancias em metros)
public class GeoUtil {

    private static final double RAIO_TERRA_METROS = 6371000;

    // Limites aproximados do municipio de Sao Paulo (area de cobertura do piloto)
    private static final double LAT_MIN = -24.01;
    private static final double LAT_MAX = -23.35;
    private static final double LNG_MIN = -46.83;
    private static final double LNG_MAX = -46.36;

    // Distancia em metros entre dois pontos (formula de Haversine)
    public static double distanciaMetros(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * RAIO_TERRA_METROS * Math.asin(Math.sqrt(a));
    }

    // O ponto esta dentro do municipio de Sao Paulo?
    public static boolean dentroDeSaoPaulo(double lat, double lng) {
        return lat >= LAT_MIN && lat <= LAT_MAX && lng >= LNG_MIN && lng <= LNG_MAX;
    }

    /**
     * Procura o trecho da rota mais proximo de um ponto.
     * Devolve um vetor: [distancia do ponto ate a rota, distancia percorrida desde a origem ate esse trecho].
     */
    public static double[] proximidadeDaRota(List<double[]> pontos, double lat, double lng) {
        double menorDistancia = Double.MAX_VALUE;
        double percorridoNoMaisProximo = 0;
        double percorrido = 0;

        // Em distancias curtas da para tratar a regiao como plana: converte graus em metros
        double metrosPorGrauLat = 111320;
        double metrosPorGrauLng = 111320 * Math.cos(Math.toRadians(lat));

        for (int i = 0; i < pontos.size() - 1; i++) {
            double ax = (pontos.get(i)[1] - lng) * metrosPorGrauLng;
            double ay = (pontos.get(i)[0] - lat) * metrosPorGrauLat;
            double bx = (pontos.get(i + 1)[1] - lng) * metrosPorGrauLng;
            double by = (pontos.get(i + 1)[0] - lat) * metrosPorGrauLat;
            double dx = bx - ax;
            double dy = by - ay;
            double comprimento = Math.hypot(dx, dy);

            // t = posicao (0 a 1) do ponto mais proximo dentro do segmento A-B
            double t = 0;
            if (comprimento > 0)
                t = Math.max(0, Math.min(1, -(ax * dx + ay * dy) / (comprimento * comprimento)));
            double distancia = Math.hypot(ax + t * dx, ay + t * dy);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                percorridoNoMaisProximo = percorrido + t * comprimento;
            }
            percorrido += comprimento;
        }
        return new double[] {menorDistancia, percorridoNoMaisProximo};
    }
}
