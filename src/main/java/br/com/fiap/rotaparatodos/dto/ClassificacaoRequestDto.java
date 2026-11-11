package br.com.fiap.rotaparatodos.dto;

import jakarta.validation.constraints.Size;

/** Entrada da classificacao automatica (POST /relatos/classificacoes). */
public class ClassificacaoRequestDto {

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
