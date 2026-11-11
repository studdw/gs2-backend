package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.TipoVoto;
import java.time.LocalDateTime;

/** Saida com os dados de um voto. Nao informa quem votou, so se o voto e de quem consulta. */
public class ValidacaoResponseDto {

    private int id;

    private int relatoId;

    private TipoVoto voto;

    private String comentario;

    private LocalDateTime dataValidacao;

    private boolean minha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelatoId() {
        return relatoId;
    }

    public void setRelatoId(int relatoId) {
        this.relatoId = relatoId;
    }

    public TipoVoto getVoto() {
        return voto;
    }

    public void setVoto(TipoVoto voto) {
        this.voto = voto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(LocalDateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }

    public boolean isMinha() {
        return minha;
    }

    public void setMinha(boolean minha) {
        this.minha = minha;
    }
}
