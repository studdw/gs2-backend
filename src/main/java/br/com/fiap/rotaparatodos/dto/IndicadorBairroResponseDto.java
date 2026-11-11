package br.com.fiap.rotaparatodos.dto;

/** Indicadores de um bairro no painel publico. */
public class IndicadorBairroResponseDto {

    private String bairro;

    private int total;

    private int ativos;

    private int resolvidos;

    private Double tempoMedioResolucaoDias;

    public IndicadorBairroResponseDto() {}

    public IndicadorBairroResponseDto(String bairro, int total, int ativos, int resolvidos, Double tempoMedioResolucaoDias) {
        this.bairro = bairro;
        this.total = total;
        this.ativos = ativos;
        this.resolvidos = resolvidos;
        this.tempoMedioResolucaoDias = tempoMedioResolucaoDias;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAtivos() {
        return ativos;
    }

    public void setAtivos(int ativos) {
        this.ativos = ativos;
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
}
