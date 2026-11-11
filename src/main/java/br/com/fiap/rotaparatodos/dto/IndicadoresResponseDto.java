package br.com.fiap.rotaparatodos.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Indicadores do painel publico (HU13). Sem nenhum dado pessoal (RN22). */
public class IndicadoresResponseDto {

    private int totalRelatos;

    private int ativos;

    private int emObra;

    private int resolvidos;

    private Double tempoMedioResolucaoDias;

    private LocalDateTime atualizadoEm;

    private List<IndicadorBairroResponseDto> porBairro;

    private List<IndicadorTipoResponseDto> porTipo;

    public IndicadoresResponseDto() {}

    public IndicadoresResponseDto(int totalRelatos, int ativos, int emObra, int resolvidos, Double tempoMedioResolucaoDias, LocalDateTime atualizadoEm, List<IndicadorBairroResponseDto> porBairro, List<IndicadorTipoResponseDto> porTipo) {
        this.totalRelatos = totalRelatos;
        this.ativos = ativos;
        this.emObra = emObra;
        this.resolvidos = resolvidos;
        this.tempoMedioResolucaoDias = tempoMedioResolucaoDias;
        this.atualizadoEm = atualizadoEm;
        this.porBairro = porBairro;
        this.porTipo = porTipo;
    }

    public int getTotalRelatos() {
        return totalRelatos;
    }

    public void setTotalRelatos(int totalRelatos) {
        this.totalRelatos = totalRelatos;
    }

    public int getAtivos() {
        return ativos;
    }

    public void setAtivos(int ativos) {
        this.ativos = ativos;
    }

    public int getEmObra() {
        return emObra;
    }

    public void setEmObra(int emObra) {
        this.emObra = emObra;
    }

    public int getResolvidos() {
        return resolvidos;
    }

    public void setResolvidos(int resolvidos) {
        this.resolvidos = resolvidos;
    }

    public Double getTempoMedioResolucaoDias() {
        return tempoMedioResolucaoDias;
    }

    public void setTempoMedioResolucaoDias(Double tempoMedioResolucaoDias) {
        this.tempoMedioResolucaoDias = tempoMedioResolucaoDias;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<IndicadorBairroResponseDto> getPorBairro() {
        return porBairro;
    }

    public void setPorBairro(List<IndicadorBairroResponseDto> porBairro) {
        this.porBairro = porBairro;
    }

    public List<IndicadorTipoResponseDto> getPorTipo() {
        return porTipo;
    }

    public void setPorTipo(List<IndicadorTipoResponseDto> porTipo) {
        this.porTipo = porTipo;
    }
}
