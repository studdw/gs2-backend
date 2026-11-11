package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.TipoVoto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada para corrigir um voto ja registrado (PUT /validacoes/{id}). */
public class ValidacaoAtualizacaoRequestDto {

    @NotNull(message = "Escolha uma opção de voto.")
    private TipoVoto voto;

    @Size(max = 200, message = "O comentário deve ter no máximo 200 caracteres.")
    private String comentario;

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
}
