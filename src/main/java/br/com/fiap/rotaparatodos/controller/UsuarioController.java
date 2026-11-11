package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dao.RelatoDao;
import br.com.fiap.rotaparatodos.dao.UsuarioDao;
import br.com.fiap.rotaparatodos.dao.ValidacaoDao;
import br.com.fiap.rotaparatodos.dto.RelatoResponseDto;
import br.com.fiap.rotaparatodos.dto.UsuarioAtualizacaoRequestDto;
import br.com.fiap.rotaparatodos.dto.UsuarioRequestDto;
import br.com.fiap.rotaparatodos.dto.UsuarioResponseDto;
import br.com.fiap.rotaparatodos.exception.*;
import br.com.fiap.rotaparatodos.model.Papel;
import br.com.fiap.rotaparatodos.model.Relato;
import br.com.fiap.rotaparatodos.model.Usuario;
import br.com.fiap.rotaparatodos.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioDao dao;
    private RelatoDao relatoDao;
    private ValidacaoDao validacaoDao;
    private ModelMapper mapper;
    private BCryptPasswordEncoder encoder;
    private AutenticacaoService autenticacao;

    public UsuarioController(UsuarioDao dao, RelatoDao relatoDao, ValidacaoDao validacaoDao, ModelMapper mapper,
                             BCryptPasswordEncoder encoder, AutenticacaoService autenticacao) {
        this.dao = dao;
        this.relatoDao = relatoDao;
        this.validacaoDao = validacaoDao;
        this.mapper = mapper;
        this.encoder = encoder;
        this.autenticacao = autenticacao;
    }

    // Cadastro aberto. Toda conta criada pela API nasce como CIDADAO.
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> cadastrar(@Valid @RequestBody UsuarioRequestDto dto,
                                                        UriComponentsBuilder builder)
            throws SQLException, EmailJaCadastradoException {
        String email = dto.getEmail().trim().toLowerCase();
        if (dao.existeEmail(email, 0))
            throw new EmailJaCadastradoException();

        Usuario usuario = mapper.map(dto, Usuario.class);
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(dto.getSenha())); // guarda so o hash da senha
        usuario.setPapel(Papel.CIDADAO);
        usuario.setDataCadastro(LocalDateTime.now());

        dao.cadastrar(usuario);

        URI uri = builder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(usuario, UsuarioResponseDto.class));
    }

    // Lista todas as contas. Somente gestor, pois sao dados pessoais.
    @GetMapping
    public List<UsuarioResponseDto> listar(@RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, NaoAutenticadoException, AcessoNegadoException {
        autenticacao.gestorObrigatorio(token);
        return dao.listar().stream()
                .map(u -> mapper.map(u, UsuarioResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscar(@PathVariable int id,
                                                     @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        autenticacao.exigirDonoOuGestor(id, solicitante);
        Usuario usuario = dao.buscar(id);
        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDto.class));
    }

    // Atualiza nome, e-mail, perfil de mobilidade e, se enviada, a senha
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizar(@PathVariable int id,
                                                        @Valid @RequestBody UsuarioAtualizacaoRequestDto dto,
                                                        @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException,
            EmailJaCadastradoException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        autenticacao.exigirDonoOuGestor(id, solicitante);

        Usuario usuario = dao.buscar(id);
        String email = dto.getEmail().trim().toLowerCase();
        if (dao.existeEmail(email, id))
            throw new EmailJaCadastradoException();

        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(email);
        usuario.setPerfilMobilidade(dto.getPerfilMobilidade());
        if (dto.getSenha() != null && !dto.getSenha().isBlank())
            usuario.setSenha(encoder.encode(dto.getSenha()));
        dao.atualizar(usuario);
        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDto.class));
    }

    // 204 ao remover; 409 se a conta ja tiver relatos ou validacoes
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id,
                                        @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException,
            RegistroComDependenciasException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        autenticacao.exigirDonoOuGestor(id, solicitante);
        dao.buscar(id); // 404 se nao existir
        if (relatoDao.contarPorUsuario(id) > 0 || validacaoDao.contarPorUsuario(id) > 0)
            throw new RegistroComDependenciasException(
                    "Esta conta tem relatos ou validações registrados e não pode ser removida.");
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }

    // Rota aninhada do 1:N usuario -> relatos ("Meus relatos")
    @GetMapping("/{id}/relatos")
    public List<RelatoResponseDto> listarRelatos(@PathVariable int id,
                                                 @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        autenticacao.exigirDonoOuGestor(id, solicitante);
        dao.buscar(id); // 404 se nao existir

        List<RelatoResponseDto> lista = new ArrayList<>();
        for (Relato relato : relatoDao.buscarPorUsuario(id)) {
            RelatoResponseDto relatoDto = mapper.map(relato, RelatoResponseDto.class);
            relatoDto.setStatus(relato.calcularStatusAtual());
            relatoDto.setConfirmacoesFaltantes(relato.calcularConfirmacoesFaltantes());
            relatoDto.setMeu(relato.pertenceA(solicitante));
            lista.add(relatoDto);
        }
        return lista;
    }

}
