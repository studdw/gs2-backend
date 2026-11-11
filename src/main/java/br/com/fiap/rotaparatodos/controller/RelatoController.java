package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dao.RelatoDao;
import br.com.fiap.rotaparatodos.dao.ValidacaoDao;
import br.com.fiap.rotaparatodos.dto.*;
import br.com.fiap.rotaparatodos.exception.*;
import br.com.fiap.rotaparatodos.model.*;
import br.com.fiap.rotaparatodos.service.AutenticacaoService;
import br.com.fiap.rotaparatodos.service.ClassificadorService;
import br.com.fiap.rotaparatodos.service.GeoUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/relatos")
public class RelatoController {

    // Maximo de relatos por usuario a cada 24 horas (RN10)
    private static final int LIMITE_RELATOS_POR_DIA = 10;
    // Raio em que um relato do mesmo tipo e considerado duplicado (RF12)
    private static final double RAIO_DUPLICIDADE_METROS = 15;
    // Distancia maxima entre quem vota e o ponto relatado (RN14)
    private static final double DISTANCIA_MAXIMA_VOTO_METROS = 100;

    private RelatoDao dao;
    private ValidacaoDao validacaoDao;
    private ModelMapper mapper;
    private ClassificadorService classificador;
    private AutenticacaoService autenticacao;

    public RelatoController(RelatoDao dao, ValidacaoDao validacaoDao, ModelMapper mapper,
                            ClassificadorService classificador, AutenticacaoService autenticacao) {
        this.dao = dao;
        this.validacaoDao = validacaoDao;
        this.mapper = mapper;
        this.classificador = classificador;
        this.autenticacao = autenticacao;
    }

    // 201 + Location; 400 dados invalidos; 401 sem login; 409 duplicado ou limite diario
    @PostMapping
    public ResponseEntity<RelatoResponseDto> cadastrar(@Valid @RequestBody RelatoRequestDto dto,
                                                       UriComponentsBuilder builder,
                                                       @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, NaoAutenticadoException, RegraNegocioException {
        Usuario autor = autenticacao.usuarioObrigatorio(token);

        // RN10 - antispam: no maximo 10 relatos por usuario em 24 horas
        if (dao.contarPorUsuarioDesde(autor.getId(), LocalDateTime.now().minusHours(24)) >= LIMITE_RELATOS_POR_DIA)
            throw new LimiteRelatosException(LIMITE_RELATOS_POR_DIA);

        exigirDentroDeSaoPaulo(dto.getLatitude(), dto.getLongitude());

        // RF12 - nao cria duplicata: mesmo tipo, ainda ativo, a menos de 15 metros
        for (Relato existente : dao.listar()) {
            double distancia = GeoUtil.distanciaMetros(dto.getLatitude(), dto.getLongitude(),
                    existente.getLatitude(), existente.getLongitude());
            if (existente.getTipo() == dto.getTipo() && existente.estaAtivo() && distancia <= RAIO_DUPLICIDADE_METROS)
                throw new RelatoDuplicadoException(existente.getProtocolo());
        }

        // RN11 - a confianca da classificacao automatica decide se publica direto ou vai para moderacao
        ClassificacaoResponseDto classificacao = classificador.classificar(dto.getDescricao());

        Relato relato = mapper.map(dto, Relato.class);
        relato.setUsuario(autor);
        relato.setEndereco(dto.getEndereco().trim());
        relato.setBairro(dto.getBairro().trim());
        relato.setConfiancaIa(classificacao.getConfianca());
        relato.setStatus(classificacao.isVaiParaModeracao() ? StatusRelato.EM_MODERACAO : StatusRelato.PUBLICADO);
        relato.setDataCriacao(LocalDateTime.now());
        relato.setPossuiFoto(relato.getFoto() != null);

        dao.cadastrar(relato);

        URI uri = builder.path("/relatos/{id}").buildAndExpand(relato.getId()).toUri();
        return ResponseEntity.created(uri).body(paraDto(relato, autor));
    }

    /**
     * Lista publica com filtros opcionais. Com latitude, longitude e raio (em metros),
     * devolve so os relatos ativos perto do ponto, do mais perto para o mais longe ("Relatos perto de mim").
     */
    @GetMapping
    public List<RelatoResponseDto> listar(@RequestParam(required = false) StatusRelato status,
                                          @RequestParam(required = false) String bairro,
                                          @RequestParam(required = false) TipoObstaculo tipo,
                                          @RequestParam(required = false) Gravidade gravidade,
                                          @RequestParam(required = false) Double latitude,
                                          @RequestParam(required = false) Double longitude,
                                          @RequestParam(required = false) Double raio,
                                          @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException {
        Usuario solicitante = autenticacao.usuarioOpcional(token);
        boolean gestor = solicitante != null && solicitante.isGestor();
        boolean pertoDeUmPonto = latitude != null && longitude != null;
        double raioMetros = raio == null ? 200 : raio;

        List<RelatoResponseDto> lista = new ArrayList<>();
        for (Relato relato : dao.listar()) {
            // Relatos em moderacao ou rejeitados so aparecem para o gestor e para o proprio autor
            if (!gestor && !relato.estaVisivelAoPublico() && !relato.pertenceA(solicitante))
                continue;
            if (status != null && relato.getStatus() != status)
                continue;
            if (bairro != null && !bairro.isBlank() && !relato.getBairro().equalsIgnoreCase(bairro.trim()))
                continue;
            if (tipo != null && relato.getTipo() != tipo)
                continue;
            if (gravidade != null && relato.getGravidade() != gravidade)
                continue;

            RelatoResponseDto relatoDto = paraDto(relato, solicitante);
            if (pertoDeUmPonto) {
                double distancia = GeoUtil.distanciaMetros(latitude, longitude, relato.getLatitude(), relato.getLongitude());
                if (!relato.estaAtivo() || distancia > raioMetros)
                    continue;
                relatoDto.setDistanciaMetros((int) Math.round(distancia));
            }
            lista.add(relatoDto);
        }
        if (pertoDeUmPonto)
            lista.sort(Comparator.comparing(RelatoResponseDto::getDistanciaMetros));
        return lista;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatoResponseDto> buscar(@PathVariable int id,
                                                    @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException {
        Usuario solicitante = autenticacao.usuarioOpcional(token);
        Relato relato = dao.buscar(id);
        boolean gestor = solicitante != null && solicitante.isGestor();
        // Para quem nao pode ver, um relato em moderacao "nao existe"
        if (!gestor && !relato.estaVisivelAoPublico() && !relato.pertenceA(solicitante))
            throw new EntidadeNaoEncontradaException("Relato não encontrado");
        return ResponseEntity.ok(paraDto(relato, solicitante));
    }

    // Correcao dos dados pelo autor ou por um gestor
    @PutMapping("/{id}")
    public ResponseEntity<RelatoResponseDto> atualizar(@PathVariable int id, @Valid @RequestBody RelatoRequestDto dto,
                                                       @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException,
            RegraNegocioException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        Relato relato = dao.buscar(id);
        exigirAutorOuGestor(relato, solicitante);
        if (relato.getStatus() == StatusRelato.RESOLVIDO)
            throw new RegraNegocioException("Este relato já foi resolvido e não pode mais ser alterado.");
        exigirDentroDeSaoPaulo(dto.getLatitude(), dto.getLongitude());

        relato.setTipo(dto.getTipo());
        relato.setGravidade(dto.getGravidade());
        relato.setDescricao(dto.getDescricao());
        relato.setEndereco(dto.getEndereco().trim());
        relato.setBairro(dto.getBairro().trim());
        relato.setLatitude(dto.getLatitude());
        relato.setLongitude(dto.getLongitude());
        dao.atualizar(relato);
        return ResponseEntity.ok(paraDto(relato, solicitante));
    }

    // Mudanca de status pelo gestor: aprovar, rejeitar, colocar em obra ou resolver (HU12 / RN13)
    @PatchMapping("/{id}/status")
    public ResponseEntity<RelatoResponseDto> alterarStatus(@PathVariable int id,
                                                           @Valid @RequestBody StatusRelatoRequestDto dto,
                                                           @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException,
            RegraNegocioException {
        Usuario gestor = autenticacao.gestorObrigatorio(token);
        Relato relato = dao.buscar(id);

        if (dto.getStatus() == StatusRelato.EXPIRADO)
            throw new RegraNegocioException("O status \"Expirado\" é calculado pelo sistema e não pode ser definido.");
        if (dto.getStatus() == StatusRelato.EM_OBRA && dto.getDataPrevisaoObra() == null)
            throw new RegraNegocioException("Informe a data prevista para a conclusão da obra.");

        relato.setStatus(dto.getStatus());
        relato.setDataPrevisaoObra(dto.getStatus() == StatusRelato.EM_OBRA ? dto.getDataPrevisaoObra() : null);
        relato.setDataResolucao(dto.getStatus() == StatusRelato.RESOLVIDO ? LocalDateTime.now() : null);
        dao.atualizar(relato);
        return ResponseEntity.ok(paraDto(relato, gestor));
    }

    // 204 ao remover; 409 se o relato ja tiver validacoes da comunidade
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id,
                                        @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, AcessoNegadoException,
            RegistroComDependenciasException {
        Usuario solicitante = autenticacao.usuarioObrigatorio(token);
        Relato relato = dao.buscar(id);
        exigirAutorOuGestor(relato, solicitante);
        if (validacaoDao.contarPorRelato(id) > 0)
            throw new RegistroComDependenciasException(
                    "Este relato já recebeu validações da comunidade e não pode ser removido.");
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }

    // Sugestao automatica de tipo e gravidade antes de enviar o relato (HU07)
    @PostMapping("/classificacoes")
    public ClassificacaoResponseDto classificar(@Valid @RequestBody ClassificacaoRequestDto dto) {
        return classificador.classificar(dto.getDescricao());
    }

    // Rota aninhada do 1:N relato -> validacoes: lista os votos de um relato
    @GetMapping("/{id}/validacoes")
    public List<ValidacaoResponseDto> listarValidacoes(@PathVariable int id,
                                                       @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException {
        Usuario solicitante = autenticacao.usuarioOpcional(token);
        dao.buscar(id); // 404 se o relato nao existir
        List<ValidacaoResponseDto> lista = new ArrayList<>();
        for (Validacao validacao : validacaoDao.buscarPorRelato(id)) {
            ValidacaoResponseDto validacaoDto = mapper.map(validacao, ValidacaoResponseDto.class);
            validacaoDto.setMinha(validacao.pertenceA(solicitante));
            lista.add(validacaoDto);
        }
        return lista;
    }

    // Rota aninhada do 1:N: registra um voto. 201 + Location; 404 relato inexistente; 409 regra violada.
    @PostMapping("/{id}/validacoes")
    public ResponseEntity<ValidacaoResponseDto> validar(@PathVariable int id, @Valid @RequestBody ValidacaoRequestDto dto,
                                                        UriComponentsBuilder builder,
                                                        @RequestHeader(value = "Authorization", required = false) String token)
            throws SQLException, EntidadeNaoEncontradaException, NaoAutenticadoException, ValidacaoNaoPermitidaException {
        Usuario usuario = autenticacao.usuarioObrigatorio(token);
        Relato relato = dao.buscar(id);

        // RN07/RN17 - so relatos ativos recebem votos
        if (!relato.estaAtivo())
            throw new ValidacaoNaoPermitidaException("Este relato não está mais ativo e não recebe novos votos.");
        // RN16 - o autor nao valida o proprio relato
        if (relato.pertenceA(usuario))
            throw new ValidacaoNaoPermitidaException("Você não pode validar o próprio relato. Peça a alguém que passe por aqui.");
        // Votos independentes: cada pessoa vota uma unica vez por relato (RN15)
        if (validacaoDao.existeVoto(id, usuario.getId()))
            throw new ValidacaoNaoPermitidaException("Você já votou neste relato. Obrigado por ajudar!");
        // RN14 - precisa estar a ate 100 metros do ponto
        double distancia = GeoUtil.distanciaMetros(dto.getLatitude(), dto.getLongitude(),
                relato.getLatitude(), relato.getLongitude());
        if (distancia > DISTANCIA_MAXIMA_VOTO_METROS)
            throw new ValidacaoNaoPermitidaException("Aproxime-se do local (até 100 m) para validar.");

        Validacao validacao = new Validacao(0, relato, usuario, dto.getVoto(), dto.getComentario(), LocalDateTime.now());
        validacaoDao.cadastrar(validacao);

        // RN15 - com 3 votos iguais o relato muda de status
        int aindaExiste = validacaoDao.contarPorRelatoEVoto(id, TipoVoto.AINDA_EXISTE);
        int foiResolvido = validacaoDao.contarPorRelatoEVoto(id, TipoVoto.FOI_RESOLVIDO);
        if (relato.recalcularStatus(aindaExiste, foiResolvido))
            dao.atualizar(relato);

        URI uri = builder.path("/validacoes/{id}").buildAndExpand(validacao.getId()).toUri();
        ValidacaoResponseDto validacaoDto = mapper.map(validacao, ValidacaoResponseDto.class);
        validacaoDto.setMinha(true);
        return ResponseEntity.created(uri).body(validacaoDto);
    }

    // Converte o model no DTO de saida e preenche os campos calculados
    private RelatoResponseDto paraDto(Relato relato, Usuario solicitante) {
        RelatoResponseDto relatoDto = mapper.map(relato, RelatoResponseDto.class);
        relatoDto.setStatus(relato.calcularStatusAtual());
        relatoDto.setConfirmacoesFaltantes(relato.calcularConfirmacoesFaltantes());
        relatoDto.setMeu(relato.pertenceA(solicitante));
        return relatoDto;
    }

    private void exigirAutorOuGestor(Relato relato, Usuario solicitante) throws AcessoNegadoException {
        if (!solicitante.isGestor() && !relato.pertenceA(solicitante))
            throw new AcessoNegadoException("Somente quem criou o relato ou um gestor pode alterá-lo.");
    }

    private void exigirDentroDeSaoPaulo(double latitude, double longitude) throws RegraNegocioException {
        if (!GeoUtil.dentroDeSaoPaulo(latitude, longitude))
            throw new RegraNegocioException("Por enquanto só recebemos relatos dentro do município de São Paulo.");
    }

}
