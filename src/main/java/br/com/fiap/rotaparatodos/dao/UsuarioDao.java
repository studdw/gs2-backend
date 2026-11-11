package br.com.fiap.rotaparatodos.dao;

import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.model.Papel;
import br.com.fiap.rotaparatodos.model.PerfilMobilidade;
import br.com.fiap.rotaparatodos.model.Usuario;
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
public class UsuarioDao {

    private final DataSource dataSource;

    private static final String INSERT_SQL = "insert into tb_usuario (cd_usuario, nm_usuario, ds_email, ds_senha, " +
            "tp_papel, tp_perfil_mobilidade, dt_cadastro) values (sq_tb_usuario.nextval, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "select * from tb_usuario order by nm_usuario";
    private static final String SELECT_BY_ID_SQL = "select * from tb_usuario where cd_usuario = ?";
    private static final String SELECT_BY_EMAIL_SQL = "select * from tb_usuario where lower(ds_email) = lower(?)";
    private static final String UPDATE_SQL = "update tb_usuario set nm_usuario = ?, ds_email = ?, ds_senha = ?, " +
            "tp_perfil_mobilidade = ? where cd_usuario = ?";
    private static final String DELETE_SQL = "delete from tb_usuario where cd_usuario = ?";

    public UsuarioDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Usuario usuario) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_usuario"})) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPapel().name());
            stmt.setString(5, usuario.getPerfilMobilidade() == null ? null : usuario.getPerfilMobilidade().name());
            stmt.setObject(6, usuario.getDataCadastro());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next())
                usuario.setId(resultSet.getInt(1));
        }
    }

    public List<Usuario> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Usuario> lista = new ArrayList<>();
            while (resultSet.next())
                lista.add(getUsuario(resultSet));
            return lista;
        }
    }

    public Usuario buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                throw new EntidadeNaoEncontradaException("Usuário não encontrado");
            return getUsuario(resultSet);
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_EMAIL_SQL)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                throw new EntidadeNaoEncontradaException("Usuário não encontrado");
            return getUsuario(resultSet);
        }
    }

    // Existe outra conta (diferente de idIgnorado) com este e-mail? Use idIgnorado = 0 no cadastro.
    public boolean existeEmail(String email, int idIgnorado) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_EMAIL_SQL)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt("cd_usuario") != idIgnorado;
        }
    }

    public void atualizar(Usuario usuario) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfilMobilidade() == null ? null : usuario.getPerfilMobilidade().name());
            stmt.setInt(5, usuario.getId());
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0)
                throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        }
    }

    private Usuario getUsuario(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("cd_usuario");
        String nome = resultSet.getString("nm_usuario");
        String email = resultSet.getString("ds_email");
        String senha = resultSet.getString("ds_senha");
        Papel papel = Papel.valueOf(resultSet.getString("tp_papel"));
        String perfil = resultSet.getString("tp_perfil_mobilidade");
        PerfilMobilidade perfilMobilidade = perfil == null ? null : PerfilMobilidade.valueOf(perfil);
        LocalDateTime dataCadastro = resultSet.getObject("dt_cadastro", LocalDateTime.class);
        return new Usuario(id, nome, email, senha, papel, perfilMobilidade, dataCadastro);
    }

}
