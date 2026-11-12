package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dao.ContatoDao;
import br.com.fiap.rotaparatodos.dto.ContatoRequestDto;
import br.com.fiap.rotaparatodos.dto.ContatoResponseDto;
import br.com.fiap.rotaparatodos.dto.StatusContatoRequestDto;
import br.com.fiap.rotaparatodos.exception.AcessoNegadoException;
import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.exception.LimiteContatosException;
import br.com.fiap.rotaparatodos.exception.NaoAutenticadoException;
import br.com.fiap.rotaparatodos.model.Contato;
import br.com.fiap.rotaparatodos.model.StatusContato;
import br.com.fiap.rotaparatodos.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

// Mensagens da pagina Contato. Qualquer pessoa envia; so o gestor le, muda o status e remove.
@RestController
@RequestMapping("/contatos")
public class ContatoController {

    // Antispam: no maximo 3 mensagens por e-mail a cada 1 hora
    private static final int LIMITE_POR_HORA = 3;

    private ContatoDao dao;
    private AutenticacaoService autenticacao;

    public ContatoController(ContatoDao dao, AutenticacaoService autenticacao) {
        this.dao = dao;
        this.autenticacao = autenticacao;
    }

    // Envio aberto. Se a pessoa estiver logada, a mensagem fica ligada a conta dela.
    @PostMapping
    public ResponseEntity<ContatoResponseDto> cadastrar(@Valid @RequestBody ContatoRequestDto dto,
                                                        @RequestHeader(value = "Authorization", required = false) String token,
                                                        UriComponentsBuilder builder)
            throws SQLException, LimiteContatosException {
        String email = dto.getEmail().trim().toLowerCase();
        LocalDateTime agora = LocalDateTime.now();
        if (dao.contarPorEmailDesde(email, agora.minusHours(1)) >= LIMITE_POR_HORA)
            throw new LimiteContatosException(LIMITE_POR_HORA);

        Contato contato = new Contato();
        contato.setUsuario(autenticacao.usuarioOpcional(token));
        contato.setNome(dto.getNome().trim());
        contato.setEmail(email);
        contato.setAssunto(dto.getAssunto());
        contato.setMensagem(dto.getMensagem().trim());
        contato.setStatus(StatusContato.ABERTO);
        contato.setDataEnvio(agora);

        dao.cadastrar(contato);

        URI uri = builder.path("/contatos/{id}").buildAndExpand(contato.getId()).toUri();
        return ResponseEntity.created(uri).body(paraDto(contato));
    }

    @GetMapping
    public List<ContatoResponseDto> listar(@RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, NaoAutenticadoException, AcessoNegadoException {
        autenticacao.gestorObrigatorio(token);
        return dao.listar().stream().map(this::paraDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContatoResponseDto> buscar(@PathVariable int id,
                                                     @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        autenticacao.gestorObrigatorio(token);
        return ResponseEntity.ok(paraDto(dao.buscar(id)));
    }

    // O gestor marca a mensagem como respondida ou arquivada
    @PatchMapping("/{id}/status")
    public ResponseEntity<ContatoResponseDto> atualizarStatus(@PathVariable int id,
                                                              @Valid @RequestBody StatusContatoRequestDto dto,
                                                              @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        autenticacao.gestorObrigatorio(token);
        dao.atualizarStatus(id, dto.getStatus());
        return ResponseEntity.ok(paraDto(dao.buscar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id,
                                        @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException {
        autenticacao.gestorObrigatorio(token);
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }

    // Conversao feita a mao: com o ModelMapper, "nome" e "email" seriam ambiguos com usuario.nome e usuario.email
    private ContatoResponseDto paraDto(Contato contato) {
        ContatoResponseDto dto = new ContatoResponseDto();
        dto.setId(contato.getId());
        dto.setUsuarioId(contato.getUsuario() != null ? contato.getUsuario().getId() : null);
        dto.setNome(contato.getNome());
        dto.setEmail(contato.getEmail());
        dto.setAssunto(contato.getAssunto());
        dto.setMensagem(contato.getMensagem());
        dto.setStatus(contato.getStatus());
        dto.setDataEnvio(contato.getDataEnvio());
        return dto;
    }

}
