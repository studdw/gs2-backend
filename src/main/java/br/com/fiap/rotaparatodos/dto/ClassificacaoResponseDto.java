package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;

/** Sugestao da classificacao automatica: tipo, gravidade e confianca (0 a 100). */
public class ClassificacaoResponseDto {

    private TipoObstaculo tipoSugerido;

    private Gravidade gravidadeSugerida;

    private int confianca;

    private boolean vaiParaModeracao;

    public ClassificacaoResponseDto() {}

    public ClassificacaoResponseDto(TipoObstaculo tipoSugerido, Gravidade gravidadeSugerida, int confianca, boolean vaiParaModeracao) {
        this.tipoSugerido = tipoSugerido;
        this.gravidadeSugerida = gravidadeSugerida;
        this.confianca = confianca;
        this.vaiParaModeracao = vaiParaModeracao;
    }

    public TipoObstaculo getTipoSugerido() {
        return tipoSugerido;
    }

    public void setTipoSugerido(TipoObstaculo tipoSugerido) {
        this.tipoSugerido = tipoSugerido;
    }

    public Gravidade getGravidadeSugerida() {
        return gravidadeSugerida;
    }

    public void setGravidadeSugerida(Gravidade gravidadeSugerida) {
        this.gravidadeSugerida = gravidadeSugerida;
    }

    public int getConfianca() {
        return confianca;
    }

    public void setConfianca(int confianca) {
        this.confianca = confianca;
    }

    public boolean isVaiParaModeracao() {
        return vaiParaModeracao;
    }

    public void setVaiParaModeracao(boolean vaiParaModeracao) {
        this.vaiParaModeracao = vaiParaModeracao;
    }
}
