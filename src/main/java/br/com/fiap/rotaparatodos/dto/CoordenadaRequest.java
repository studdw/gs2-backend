package br.com.fiap.rotaparatodos.dto;

/** Corpo JSON enviado ao POST /zona-critica: {"latitude": ..., "longitude": ...} */
public record CoordenadaRequest(double latitude, double longitude) {
}
