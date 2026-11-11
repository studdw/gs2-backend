package br.com.fiap.rotaparatodos.dao;

import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.model.Relato;
import br.com.fiap.rotaparatodos.model.TipoVoto;
import br.com.fiap.rotaparatodos.model.Usuario;
import br.com.fiap.rotaparatodos.model.Validacao;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ValidacaoDao {

    private final DataSource dataSource;

    private static final String INSERT_SQL = "insert into tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, " +
            "ds_comentario, dt_validacao) values (sq_tb_validacao.nextval, ?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "select * from tb_validacao order by dt_validacao desc";
    private static final String SELECT_BY_ID_SQL = "select * from tb_validacao where cd_validacao = ?";
    private static final String SELECT_BY_RELATO_SQL = "select * from tb_validacao where cd_relato = ? order by dt_validacao desc";
    private static final String UPDATE_SQL = "update tb_validacao set tp_voto = ?, ds_comentario = ? where cd_validacao = ?";
    private static final String DELETE_SQL = "delete from tb_validacao where cd_validacao = ?";
    private static final String COUNT_VOTO_DO_USUARIO_SQL = "select count(*) from tb_validacao where cd_relato = ? and cd_usuario = ?";
    private static final String COUNT_BY_RELATO_SQL = "select count(*) from tb_validacao where cd_relato = ?";
    private static final String COUNT_BY_RELATO_E_VOTO_SQL = "select count(*) from tb_validacao where cd_relato = ? and tp_voto = ?";
    private static final String COUNT_BY_USUARIO_SQL = "select count(*) from tb_validacao where cd_usuario = ?";

    public ValidacaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Validacao validacao) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_validacao"})) {
            stmt.setInt(1, validacao.getRelato().getId());
            stmt.setInt(2, validacao.getUsuario().getId());
            stmt.setString(3, validacao.getVoto().name());
            stmt.setString(4, validacao.getComentario());
            stmt.setObject(5, validacao.getDataValidacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next())
                validacao.setId(resultSet.getInt(1));
        }
    }

    public List<Validacao> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Validacao> lista = new ArrayList<>();
            while (resultSet.next())
                lista.add(getValidacao(resultSet));
            return lista;
        }
    }

    // Lado N do relacionamento 1:N relato -> validacoes
    public List<Validacao> buscarPorRelato(int idRelato) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_RELATO_SQL)) {
            stmt.setInt(1, idRelato);
            ResultSet resultSet = stmt.executeQuery();
            List<Validacao> lista = new ArrayList<>();
            while (resultSet.next())
                lista.add(getValidacao(resultSet));
            return lista;
        }
    }

    public Validacao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                throw new EntidadeNaoEncontradaException("Validação não encontrada");
            return getValidacao(resultSet);
        }
    }

    public void atualizar(Validacao validacao) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, validacao.getVoto().name());
            stmt.setString(2, validacao.getComentario());
            stmt.setInt(3, validacao.getId());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Validação não encontrada");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Validação não encontrada");
        }
    }

    // O usuario ja votou neste relato? Cada pessoa vota uma unica vez por relato.
    public boolean existeVoto(int idRelato, int idUsuario) throws SQLException {
        return contar(COUNT_VOTO_DO_USUARIO_SQL, idRelato, idUsuario) > 0;
    }

    public int contarPorRelato(int idRelato) throws SQLException {
        return contar(COUNT_BY_RELATO_SQL, idRelato, null);
    }

    public int contarPorRelatoEVoto(int idRelato, TipoVoto voto) throws SQLException {
        return contar(COUNT_BY_RELATO_E_VOTO_SQL, idRelato, voto.name());
    }

    public int contarPorUsuario(int idUsuario) throws SQLException {
        return contar(COUNT_BY_USUARIO_SQL, idUsuario, null);
    }

    // Executa um "select count(*)" com um ou dois parametros
    private int contar(String sql, int primeiro, Object segundo) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, primeiro);
            if (segundo != null)
                stmt.setObject(2, segundo);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private Validacao getValidacao(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("cd_validacao");
        int idRelato = resultSet.getInt("cd_relato");
        int idUsuario = resultSet.getInt("cd_usuario");
        TipoVoto voto = TipoVoto.valueOf(resultSet.getString("tp_voto"));
        String comentario = resultSet.getString("ds_comentario");
        LocalDateTime dataValidacao = resultSet.getObject("dt_validacao", LocalDateTime.class);
        return new Validacao(id, new Relato(idRelato), new Usuario(idUsuario), voto, comentario, dataValidacao);
    }

}
