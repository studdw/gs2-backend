package br.com.fiap.rotaparatodos.dao;

import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.model.AssuntoContato;
import br.com.fiap.rotaparatodos.model.Contato;
import br.com.fiap.rotaparatodos.model.StatusContato;
import br.com.fiap.rotaparatodos.model.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContatoDao {

    private final DataSource dataSource;

    private static final String INSERT_SQL = "insert into tb_contato (cd_contato, cd_usuario, nm_contato, ds_email, " +
            "tp_assunto, ds_mensagem, st_contato, dt_envio) values (sq_tb_contato.nextval, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "select * from tb_contato order by dt_envio desc";
    private static final String SELECT_BY_ID_SQL = "select * from tb_contato where cd_contato = ?";
    private static final String UPDATE_STATUS_SQL = "update tb_contato set st_contato = ? where cd_contato = ?";
    private static final String DELETE_SQL = "delete from tb_contato where cd_contato = ?";
    private static final String COUNT_BY_EMAIL_DESDE_SQL = "select count(*) from tb_contato where ds_email = ? and dt_envio >= ?";

    public ContatoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Contato contato) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_contato"})) {
            if (contato.getUsuario() != null)
                stmt.setInt(1, contato.getUsuario().getId());
            else
                stmt.setNull(1, Types.NUMERIC);
            stmt.setString(2, contato.getNome());
            stmt.setString(3, contato.getEmail());
            stmt.setString(4, contato.getAssunto().name());
            stmt.setString(5, contato.getMensagem());
            stmt.setString(6, contato.getStatus().name());
            stmt.setObject(7, contato.getDataEnvio());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next())
                contato.setId(resultSet.getInt(1));
        }
    }

    public List<Contato> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Contato> lista = new ArrayList<>();
            while (resultSet.next())
                lista.add(getContato(resultSet));
            return lista;
        }
    }

    public Contato buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                throw new EntidadeNaoEncontradaException("Mensagem de contato não encontrada");
            return getContato(resultSet);
        }
    }

    public void atualizarStatus(int id, StatusContato status) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Mensagem de contato não encontrada");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Mensagem de contato não encontrada");
        }
    }

    // Quantas mensagens um e-mail enviou a partir de uma data/hora (regra antispam)
    public int contarPorEmailDesde(String email, LocalDateTime desde) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(COUNT_BY_EMAIL_DESDE_SQL)) {
            stmt.setString(1, email);
            stmt.setObject(2, desde);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private Contato getContato(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("cd_contato");
        int idUsuario = resultSet.getInt("cd_usuario");
        Usuario usuario = resultSet.wasNull() ? null : new Usuario(idUsuario);
        String nome = resultSet.getString("nm_contato");
        String email = resultSet.getString("ds_email");
        AssuntoContato assunto = AssuntoContato.valueOf(resultSet.getString("tp_assunto"));
        String mensagem = resultSet.getString("ds_mensagem");
        StatusContato status = StatusContato.valueOf(resultSet.getString("st_contato"));
        LocalDateTime dataEnvio = resultSet.getObject("dt_envio", LocalDateTime.class);
        return new Contato(id, usuario, nome, email, assunto, mensagem, status, dataEnvio);
    }

}
