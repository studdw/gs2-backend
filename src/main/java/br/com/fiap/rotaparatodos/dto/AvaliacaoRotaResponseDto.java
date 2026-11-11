package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.PerfilMobilidade;
import java.util.List;

/** Saida da avaliacao: as rotas ordenadas da mais para a menos acessivel. */
public class AvaliacaoRotaResponseDto {

    private PerfilMobilidade perfil;

    private boolean periodoNoturno;

    private boolean existeRotaAcessivel;

    private String aviso;

    private List<RotaAvaliadaResponseDto> rotas;

    public AvaliacaoRotaResponseDto() {}

    public AvaliacaoRotaResponseDto(PerfilMobilidade perfil, boolean periodoNoturno, boolean existeRotaAcessivel, String aviso, List<RotaAvaliadaResponseDto> rotas) {
        this.perfil = perfil;
        this.periodoNoturno = periodoNoturno;
        this.existeRotaAcessivel = existeRotaAcessivel;
        this.aviso = aviso;
        this.rotas = rotas;
    }

    public PerfilMobilidade getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilMobilidade perfil) {
        this.perfil = perfil;
    }

    public boolean isPeriodoNoturno() {
        return periodoNoturno;
    }

    public void setPeriodoNoturno(boolean periodoNoturno) {
        this.periodoNoturno = periodoNoturno;
    }

    public boolean isExisteRotaAcessivel() {
        return existeRotaAcessivel;
    }

    public void setExisteRotaAcessivel(boolean existeRotaAcessivel) {
        this.existeRotaAcessivel = existeRotaAcessivel;
    }

    public String getAviso() {
        return aviso;
    }

    public void setAviso(String aviso) {
        this.aviso = aviso;
    }

    public List<RotaAvaliadaResponseDto> getRotas() {
        return rotas;
    }

    public void setRotas(List<RotaAvaliadaResponseDto> rotas) {
        this.rotas = rotas;
    }
}
