package br.com.fiap.rotaparatodos.dto;

import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.StatusRelato;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Saida com os dados de um relato. Nao informa quem e o autor (RN20/RN22), so se o relato e de quem consulta. */
public class RelatoResponseDto {

    private int id;

    private String protocolo;

    private TipoObstaculo tipo;

    private Gravidade gravidade;

    private String descricao;

    private String endereco;

    private String bairro;

    private double latitude;

    private double longitude;

    private int confiancaIa;

    private StatusRelato status;

    private boolean possuiFoto;

    private String foto;

    private LocalDateTime dataCriacao;

    private LocalDate dataPrevisaoObra;

    private LocalDateTime dataResolucao;

    private int confirmacoes;

    private int votosResolvido;

    // Campos calculados no controller
    private int confirmacoesFaltantes;

    private boolean meu;

    private Integer distanciaMetros;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public TipoObstaculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoObstaculo tipo) {
        this.tipo = tipo;
    }

    public Gravidade getGravidade() {
        return gravidade;
    }

    public void setGravidade(Gravidade gravidade) {
        this.gravidade = gravidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getConfiancaIa() {
        return confiancaIa;
    }

    public void setConfiancaIa(int confiancaIa) {
        this.confiancaIa = confiancaIa;
    }

    public StatusRelato getStatus() {
        return status;
    }

    public void setStatus(StatusRelato status) {
        this.status = status;
    }

    public boolean isPossuiFoto() {
        return possuiFoto;
    }

    public void setPossuiFoto(boolean possuiFoto) {
        this.possuiFoto = possuiFoto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataPrevisaoObra() {
        return dataPrevisaoObra;
    }

    public void setDataPrevisaoObra(LocalDate dataPrevisaoObra) {
        this.dataPrevisaoObra = dataPrevisaoObra;
    }

    public LocalDateTime getDataResolucao() {
        return dataResolucao;
    }

    public void setDataResolucao(LocalDateTime dataResolucao) {
        this.dataResolucao = dataResolucao;
    }

    public int getConfirmacoes() {
        return confirmacoes;
    }

    public void setConfirmacoes(int confirmacoes) {
        this.confirmacoes = confirmacoes;
    }

    public int getVotosResolvido() {
        return votosResolvido;
    }

    public void setVotosResolvido(int votosResolvido) {
        this.votosResolvido = votosResolvido;
    }

    public int getConfirmacoesFaltantes() {
        return confirmacoesFaltantes;
    }

    public void setConfirmacoesFaltantes(int confirmacoesFaltantes) {
        this.confirmacoesFaltantes = confirmacoesFaltantes;
    }

    public boolean isMeu() {
        return meu;
    }

    public void setMeu(boolean meu) {
        this.meu = meu;
    }

    public Integer getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(Integer distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }
}
