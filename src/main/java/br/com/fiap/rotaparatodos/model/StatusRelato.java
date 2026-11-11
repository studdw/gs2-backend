package br.com.fiap.rotaparatodos.model;

/**
 * Ciclo de vida de um relato.
 * EXPIRADO nao e gravado no banco: e calculado quando o relato fica 90 dias sem confirmacao (RN17).
 */
public enum StatusRelato {
    EM_MODERACAO,
    PUBLICADO,
    CONFIRMADO,
    EM_OBRA,
    RESOLVIDO,
    REJEITADO,
    EXPIRADO
}
