package br.com.fiap.rotaparatodos.service;

import br.com.fiap.rotaparatodos.dao.UsuarioDao;
import br.com.fiap.rotaparatodos.exception.AcessoNegadoException;
import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.exception.NaoAutenticadoException;
import br.com.fiap.rotaparatodos.model.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Descobre quem esta chamando a API a partir do cabecalho "Authorization: Bearer <token>".
 * Os controllers recebem esse cabecalho com @RequestHeader e chamam um destes metodos.
 */
@Service
public class AutenticacaoService {

    private UsuarioDao usuarioDao;
    private TokenService tokenService;

    public AutenticacaoService(UsuarioDao usuarioDao, TokenService tokenService) {
        this.usuarioDao = usuarioDao;
        this.tokenService = tokenService;
    }

    // Usuario do token, ou null se nao houver token valido (rotas que nao exigem login)
    public Usuario usuarioOpcional(String authorization) throws SQLException {
        if (authorization == null || !authorization.startsWith("Bearer "))
            return null;
        int id = tokenService.validar(authorization.substring(7).trim());
        if (id == 0)
            return null;
        try {
            return usuarioDao.buscar(id);
        } catch (EntidadeNaoEncontradaException e) {
            return null; // a conta foi removida depois que o token foi gerado
        }
    }

    // Usuario do token. Sem token valido, lanca 401.
    public Usuario usuarioObrigatorio(String authorization) throws SQLException, NaoAutenticadoException {
        Usuario usuario = usuarioOpcional(authorization);
        if (usuario == null)
            throw new NaoAutenticadoException("Entre na sua conta para continuar.");
        return usuario;
    }

    // Gestor do token. Se a pessoa nao for gestora, lanca 403.
    public Usuario gestorObrigatorio(String authorization)
            throws SQLException, NaoAutenticadoException, AcessoNegadoException {
        Usuario usuario = usuarioObrigatorio(authorization);
        if (!usuario.isGestor())
            throw new AcessoNegadoException("Somente gestores públicos podem fazer esta operação.");
        return usuario;
    }

    // Dados pessoais so podem ser vistos ou alterados pelo proprio usuario ou por um gestor
    public void exigirDonoOuGestor(int idDoDono, Usuario solicitante) throws AcessoNegadoException {
        if (!solicitante.isGestor() && solicitante.getId() != idDoDono)
            throw new AcessoNegadoException("Você só pode acessar os seus próprios dados.");
    }
}
