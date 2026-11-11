package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dao.RelatoDao;
import br.com.fiap.rotaparatodos.dao.ValidacaoDao;
import br.com.fiap.rotaparatodos.dto.ValidacaoAtualizacaoRequestDto;
import br.com.fiap.rotaparatodos.dto.ValidacaoResponseDto;
import br.com.fiap.rotaparatodos.exception.AcessoNegadoException;
import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.exception.NaoAutenticadoException;
import br.com.fiap.rotaparatodos.model.Relato;
import br.com.fiap.rotaparatodos.model.TipoVoto;
import br.com.fiap.rotaparatodos.model.Usuario;
import br.com.fiap.rotaparatodos.model.Validacao;
import br.com.fiap.rotaparatodos.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Consulta, correcao e remocao de validacoes. O cadastro e feito pela rota aninhada POST /relatos/{id}/validacoes.
@RestController
@RequestMapping("/validacoes")
public class ValidacaoController {

    private ValidacaoDao dao;
    private RelatoDao relatoDao;
    private ModelMapper mapper;
    private AutenticacaoService autenticacao;

    public ValidacaoController(ValidacaoDao dao, RelatoDao relatoDao, ModelMapper mapper,
                               AutenticacaoService autenticacao) {
        this.dao = dao;
        this.relatoDao = relatoDao;
        this.mapper = mapper;
        this.autenticacao = autenticacao;
    }

    @GetMapping
    public List<ValidacaoResponseDto> listar(@RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException {
        Usuario solicitante = autenticacao.usuarioOpcional(token);
        List<ValidacaoResponseDto> lista = new ArrayList<>();
        for (Validacao validacao : dao.listar())
            lista.add(paraDto(validacao, solicitante));
        return lista;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidacaoResponseDto> buscar(@PathVariable int id,
                                                       @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException {
        Usuario solicitante = autenticacao.usuarioOpcional(token);
        return ResponseEntity.ok(paraDto(dao.buscar(id), solicitante));
    }

    // Quem votou pode corrigir o proprio voto
    @PutMapping("/{id}")
    public ResponseEntity<ValidacaoResponseDto> atualizar(@PathVariable int id,
                                                          @Valid @RequestBody ValidacaoAtualizacaoRequestDto dto,
                                                          @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        Validacao validacao = dao.buscar(id);
        exigirDonoOuGestor(validacao, solicitante);

        validacao.setVoto(dto.getVoto());
        validacao.setComentario(dto.getComentario());
        dao.atualizar(validacao);

        // O voto mudou: confere se o relato atingiu 3 votos iguais (RN15)
        Relato relato = relatoDao.buscar(validacao.getRelato().getId());
        int aindaExiste = dao.contarPorRelatoEVoto(relato.getId(), TipoVoto.AINDA_EXISTE);
        int foiResolvido = dao.contarPorRelatoEVoto(relato.getId(), TipoVoto.FOI_RESOLVIDO);
        if (relato.recalcularStatus(aindaExiste, foiResolvido))
            relatoDao.atualizar(relato);

        return ResponseEntity.ok(paraDto(validacao, solicitante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id,
                                        @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        exigirDonoOuGestor(dao.buscar(id), solicitante);
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }

    private ValidacaoResponseDto paraDto(Validacao validacao, Usuario solicitante) {
        ValidacaoResponseDto validacaoDto = mapper.map(validacao, ValidacaoResponseDto.class);
        validacaoDto.setMinha(validacao.pertenceA(solicitante));
        return validacaoDto;
    }

    private void exigirDonoOuGestor(Validacao validacao, Usuario solicitante) throws AcessoNegadoException {
        if (!solicitante.isGestor() && !validacao.pertenceA(solicitante))
            throw new AcessoNegadoException("Você só pode alterar os seus próprios votos.");
    }

}
