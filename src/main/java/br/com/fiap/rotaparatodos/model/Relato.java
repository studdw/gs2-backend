package br.com.fiap.rotaparatodos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Obstaculo reportado em uma calcada (tabela tb_relato).
// Um Usuario tem varios Relatos (1:N) e um Relato tem varias Validacoes (1:N).
public class Relato {

    // Dias sem nenhuma confirmacao ate o relato expirar (RN17)
    public static final int DIAS_PARA_EXPIRAR = 90;
    // Votos iguais necessarios para o relato mudar de status (RN15)
    public static final int VOTOS_PARA_MUDAR_STATUS = 3;

    private int id;
    private Usuario usuario;
    private TipoObstaculo tipo;
    private Gravidade gravidade;
    private String descricao;
    private String endereco;
    private String bairro;
    private double latitude;
    private double longitude;
    // Confianca (0 a 100) da classificacao automatica
    private int confiancaIa;
    private StatusRelato status;
    // Foto em data URL (base64). So e carregada na busca por id.
    private String foto;
    private boolean possuiFoto;
    private LocalDateTime dataCriacao;
    private LocalDate dataPrevisaoObra;
    private LocalDateTime dataResolucao;
    // Totais calculados a partir da tb_validacao (nao sao colunas da tb_relato)
    private int confirmacoes;
    private int votosResolvido;

    public Relato() {}

    public Relato(int id) {
        this.id = id;
    }

    public Relato(int id, Usuario usuario, TipoObstaculo tipo, Gravidade gravidade, String descricao,
                  String endereco, String bairro, double latitude, double longitude, int confiancaIa,
                  StatusRelato status, LocalDateTime dataCriacao) {
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
        this.gravidade = gravidade;
        this.descricao = descricao;
        this.endereco = endereco;
        this.bairro = bairro;
        this.latitude = latitude;
        this.longitude = longitude;
        this.confiancaIa = confiancaIa;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    // ---------- Regras do dominio ----------
    // (os nomes nao comecam com get/is para o ModelMapper nao confundir com campos)

    // Protocolo mostrado ao cidadao, montado a partir do ano e do id. Ex.: RT-2026-00042
    public String getProtocolo() {
        int ano = dataCriacao == null ? LocalDate.now().getYear() : dataCriacao.getYear();
        return String.format("RT-%d-%05d", ano, id);
    }

    // RN17 - relato publicado ha mais de 90 dias sem nenhuma confirmacao esta expirado
    public boolean estaExpirado() {
        return status == StatusRelato.PUBLICADO
                && confirmacoes == 0
                && dataCriacao != null
                && dataCriacao.isBefore(LocalDateTime.now().minusDays(DIAS_PARA_EXPIRAR));
    }

    // RN07 - somente relatos ativos viram alerta de rota e recebem votos
    public boolean estaAtivo() {
        boolean statusAtivo = status == StatusRelato.PUBLICADO
                || status == StatusRelato.CONFIRMADO
                || status == StatusRelato.EM_OBRA;
        return statusAtivo && !estaExpirado();
    }

    // Status mostrado para quem consulta (inclui o EXPIRADO, que e calculado)
    public StatusRelato calcularStatusAtual() {
        if (estaExpirado())
            return StatusRelato.EXPIRADO;
        return status;
    }

    // Relatos em moderacao ou rejeitados nao aparecem para o publico
    public boolean estaVisivelAoPublico() {
        return status != StatusRelato.EM_MODERACAO && status != StatusRelato.REJEITADO;
    }

    public boolean pertenceA(Usuario outro) {
        return outro != null && usuario != null && usuario.getId() == outro.getId();
    }

    // Quantas confirmacoes ainda faltam para o relato ficar CONFIRMADO
    public int calcularConfirmacoesFaltantes() {
        if (status != StatusRelato.PUBLICADO)
            return 0;
        return Math.max(0, VOTOS_PARA_MUDAR_STATUS - confirmacoes);
    }

    /**
     * RN15 - com 3 votos "Foi resolvido" o relato vira RESOLVIDO;
     * com 3 votos "Ainda existe" o relato publicado vira CONFIRMADO.
     * Devolve true quando o status mudou (e precisa ser gravado no banco).
     */
    public boolean recalcularStatus(int votosAindaExiste, int votosFoiResolvido) {
        this.confirmacoes = votosAindaExiste;
        this.votosResolvido = votosFoiResolvido;
        if (votosFoiResolvido >= VOTOS_PARA_MUDAR_STATUS && status != StatusRelato.RESOLVIDO) {
            status = StatusRelato.RESOLVIDO;
            dataResolucao = LocalDateTime.now();
            return true;
        }
        if (votosAindaExiste >= VOTOS_PARA_MUDAR_STATUS && status == StatusRelato.PUBLICADO) {
            status = StatusRelato.CONFIRMADO;
            return true;
        }
        return false;
    }

    // ---------- Getters e setters ----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isPossuiFoto() {
        return possuiFoto;
    }

    public void setPossuiFoto(boolean possuiFoto) {
        this.possuiFoto = possuiFoto;
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
}
