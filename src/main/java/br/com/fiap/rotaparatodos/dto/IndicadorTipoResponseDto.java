package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.TipoObstaculo;

/** Quantidade de relatos de um tipo de obstaculo. */
public class IndicadorTipoResponseDto {

    private TipoObstaculo tipo;

    private int total;

    public IndicadorTipoResponseDto() {}

    public IndicadorTipoResponseDto(TipoObstaculo tipo, int total) {
        this.tipo = tipo;
        this.total = total;
    }

    public TipoObstaculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoObstaculo tipo) {
        this.tipo = tipo;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
