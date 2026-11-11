package br.com.fiap.rotaparatodos.dao;

import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.Relato;
import br.com.fiap.rotaparatodos.model.StatusRelato;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import br.com.fiap.rotaparatodos.model.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RelatoDao {

    private final DataSource dataSource;

    private static final String INSERT_SQL = "insert into tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, " +
            "ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, ds_foto, dt_criacao) " +
            "values (sq_tb_relato.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // Colunas das consultas. A foto (CLOB) fica fora das listagens para nao pesar; no lugar vai o indicador fl_foto.
    // Os dois subselects contam os votos de cada relato na tb_validacao.
    private static final String COLUNAS = "select r.cd_relato, r.cd_usuario, r.tp_obstaculo, r.tp_gravidade, r.ds_relato, " +
            "r.ds_endereco, r.nm_bairro, r.vl_latitude, r.vl_longitude, r.vl_confianca_ia, r.st_relato, r.dt_criacao, " +
            "r.dt_previsao_obra, r.dt_resolucao, case when r.ds_foto is null then 0 else 1 end as fl_foto, " +
            "(select count(*) from tb_validacao v where v.cd_relato = r.cd_relato and v.tp_voto = 'AINDA_EXISTE') as qt_confirmacoes, " +
            "(select count(*) from tb_validacao v where v.cd_relato = r.cd_relato and v.tp_voto = 'FOI_RESOLVIDO') as qt_resolvidos";

    private static final String SELECT_SQL = COLUNAS + " from tb_relato r order by r.dt_criacao desc";
    private static final String SELECT_BY_USUARIO_SQL = COLUNAS + " from tb_relato r where r.cd_usuario = ? order by r.dt_criacao desc";
    private static final String SELECT_BY_ID_SQL = COLUNAS + ", r.ds_foto from tb_relato r where r.cd_relato = ?";
    private static final String UPDATE_SQL = "update tb_relato set tp_obstaculo = ?, tp_gravidade = ?, ds_relato = ?, " +
            "ds_endereco = ?, nm_bairro = ?, vl_latitude = ?, vl_longitude = ?, st_relato = ?, dt_previsao_obra = ?, " +
            "dt_resolucao = ? where cd_relato = ?";
    private static final String DELETE_SQL = "delete from tb_relato where cd_relato = ?";
    private static final String COUNT_BY_USUARIO_DESDE_SQL = "select count(*) from tb_relato where cd_usuario = ? and dt_criacao >= ?";
    private static final String COUNT_BY_USUARIO_SQL = "select count(*) from tb_relato where cd_usuario = ?";

    public RelatoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Relato relato) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_relato"})) {
            stmt.setInt(1, relato.getUsuario().getId());
            stmt.setString(2, relato.getTipo().name());
            stmt.setString(3, relato.getGravidade().name());
            stmt.setString(4, relato.getDescricao());
            stmt.setString(5, relato.getEndereco());
            stmt.setString(6, relato.getBairro());
            stmt.setDouble(7, relato.getLatitude());
            stmt.setDouble(8, relato.getLongitude());
            stmt.setInt(9, relato.getConfiancaIa());
            stmt.setString(10, relato.getStatus().name());
            if (relato.getFoto() == null) {
                stmt.setNull(11, Types.CLOB);
            } else {
                // A foto e um texto longo (base64), por isso vai como "stream" para a coluna CLOB
                stmt.setCharacterStream(11, new StringReader(relato.getFoto()), relato.getFoto().length());
            }
            stmt.setObject(12, relato.getDataCriacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next())
                relato.setId(resultSet.getInt(1));
        }
    }

    public List<Relato> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Relato> lista = new ArrayList<>();
            while (resultSet.next())
                lista.add(getRelato(resultSet));
            return lista;
        }
    }

    // Lado N do relacionamento 1:N usuario -> relatos
    public List<Relato> buscarPorUsuario(int idUsuario) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_USUARIO_SQL)) {
            stmt.setInt(1, idUsuario);
            ResultSet resultSet = stmt.executeQuery();
            List<Relato> lista = new ArrayList<>();
            while (resultSet.next())
                lista.add(getRelato(resultSet));
            return lista;
        }
    }

    // Busca o relato completo, incluindo a foto
    public Relato buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                throw new EntidadeNaoEncontradaException("Relato não encontrado");
            Relato relato = getRelato(resultSet);
            relato.setFoto(resultSet.getString("ds_foto"));
            return relato;
        }
    }

    public void atualizar(Relato relato) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, relato.getTipo().name());
            stmt.setString(2, relato.getGravidade().name());
            stmt.setString(3, relato.getDescricao());
            stmt.setString(4, relato.getEndereco());
            stmt.setString(5, relato.getBairro());
            stmt.setDouble(6, relato.getLatitude());
            stmt.setDouble(7, relato.getLongitude());
            stmt.setString(8, relato.getStatus().name());
            stmt.setObject(9, relato.getDataPrevisaoObra());
            stmt.setObject(10, relato.getDataResolucao());
            stmt.setInt(11, relato.getId());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Relato não encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Relato não encontrado");
        }
    }

    // Quantos relatos o usuario criou a partir de uma data/hora (regra antispam RN10)
    public int contarPorUsuarioDesde(int idUsuario, LocalDateTime desde) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(COUNT_BY_USUARIO_DESDE_SQL)) {
            stmt.setInt(1, idUsuario);
            stmt.setObject(2, desde);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    // Quantos relatos pertencem ao usuario (para nao remover usuario com filhos)
    public int contarPorUsuario(int idUsuario) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(COUNT_BY_USUARIO_SQL)) {
            stmt.setInt(1, idUsuario);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private Relato getRelato(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("cd_relato");
        int idUsuario = resultSet.getInt("cd_usuario");
        TipoObstaculo tipo = TipoObstaculo.valueOf(resultSet.getString("tp_obstaculo"));
        Gravidade gravidade = Gravidade.valueOf(resultSet.getString("tp_gravidade"));
        String descricao = resultSet.getString("ds_relato");
        String endereco = resultSet.getString("ds_endereco");
        String bairro = resultSet.getString("nm_bairro");
        double latitude = resultSet.getDouble("vl_latitude");
        double longitude = resultSet.getDouble("vl_longitude");
        int confiancaIa = resultSet.getInt("vl_confianca_ia");
        StatusRelato status = StatusRelato.valueOf(resultSet.getString("st_relato"));
        LocalDateTime dataCriacao = resultSet.getObject("dt_criacao", LocalDateTime.class);

        Relato relato = new Relato(id, new Usuario(idUsuario), tipo, gravidade, descricao, endereco, bairro,
                latitude, longitude, confiancaIa, status, dataCriacao);
        relato.setDataPrevisaoObra(resultSet.getObject("dt_previsao_obra", LocalDate.class));
        relato.setDataResolucao(resultSet.getObject("dt_resolucao", LocalDateTime.class));
        relato.setPossuiFoto(resultSet.getInt("fl_foto") == 1);
        relato.setConfirmacoes(resultSet.getInt("qt_confirmacoes"));
        relato.setVotosResolvido(resultSet.getInt("qt_resolvidos"));
        return relato;
    }

}
