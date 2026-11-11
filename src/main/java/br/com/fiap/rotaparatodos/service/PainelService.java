package br.com.fiap.rotaparatodos.service;

import br.com.fiap.rotaparatodos.dao.RelatoDao;
import br.com.fiap.rotaparatodos.dto.ComposicaoImpactoResponseDto;
import br.com.fiap.rotaparatodos.dto.IndicadorBairroResponseDto;
import br.com.fiap.rotaparatodos.dto.IndicadorTipoResponseDto;
import br.com.fiap.rotaparatodos.dto.IndicadoresResponseDto;
import br.com.fiap.rotaparatodos.dto.PontoCriticoResponseDto;
import br.com.fiap.rotaparatodos.dto.RelatoResponseDto;
import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.Relato;
import br.com.fiap.rotaparatodos.model.StatusRelato;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Painel publico (HU13) e ranking de pontos criticos pelo Indice de Impacto (HU11). */
@Service
public class PainelService {

    /**
     * Amostra de hospitais, unidades de saude e instituicoes de ensino da regiao central de Sao Paulo,
     * com coordenadas aproximadas. Em producao esta lista viria dos dados abertos do GeoSampa.
     */
    private static final String[] EQUIPAMENTOS = {
            "Hospital das Clínicas", "Santa Casa de São Paulo", "Hospital São Paulo",
            "Hospital do Servidor Público Estadual", "AACD Ibirapuera", "Hospital Beneficência Portuguesa",
            "FIAP Paulista", "FIAP Aclimação", "Faculdade de Direito da USP (Largo São Francisco)",
            "Biblioteca Mário de Andrade"};
    private static final double[][] COORDENADAS_EQUIPAMENTOS = {
            {-23.5571, -46.6696}, {-23.5413, -46.6497}, {-23.5982, -46.6435},
            {-23.5996, -46.6527}, {-23.5975, -46.6548}, {-23.5672, -46.6418},
            {-23.5645, -46.6525}, {-23.5740, -46.6233}, {-23.5496, -46.6373},
            {-23.5469, -46.6425}};

    // Estimativa do fluxo de pedestres por bairro (0 a 100). Valores de demonstracao.
    private static final String[] BAIRROS_COM_FLUXO = {"SÉ", "REPÚBLICA", "BELA VISTA", "LIBERDADE", "CONSOLAÇÃO",
            "PINHEIROS", "VILA MARIANA", "PARAÍSO", "ACLIMAÇÃO", "VILA CLEMENTINO"};
    private static final int[] FLUXO_DOS_BAIRROS = {100, 95, 90, 85, 85, 80, 75, 75, 60, 65};
    private static final int FLUXO_PADRAO = 50;

    private RelatoDao relatoDao;
    private ModelMapper mapper;

    public PainelService(RelatoDao relatoDao, ModelMapper mapper) {
        this.relatoDao = relatoDao;
        this.mapper = mapper;
    }

    // Indicadores gerais, por bairro e por tipo. Relatos em moderacao ou rejeitados ficam de fora.
    public IndicadoresResponseDto indicadores() throws SQLException {
        List<Relato> relatos = new ArrayList<>();
        for (Relato relato : relatoDao.listar()) {
            if (relato.estaVisivelAoPublico())
                relatos.add(relato);
        }

        // Agrupa os relatos por bairro. O TreeMap deixa os bairros em ordem alfabetica.
        Map<String, List<Relato>> porBairro = new TreeMap<>();
        for (Relato relato : relatos) {
            if (!porBairro.containsKey(relato.getBairro()))
                porBairro.put(relato.getBairro(), new ArrayList<>());
            porBairro.get(relato.getBairro()).add(relato);
        }
        List<IndicadorBairroResponseDto> indicadoresBairro = new ArrayList<>();
        for (String bairro : porBairro.keySet()) {
            List<Relato> doBairro = porBairro.get(bairro);
            indicadoresBairro.add(new IndicadorBairroResponseDto(bairro, doBairro.size(), contarAtivos(doBairro),
                    contarComStatus(doBairro, StatusRelato.RESOLVIDO), tempoMedioResolucao(doBairro)));
        }
        indicadoresBairro.sort(Comparator.comparingInt(IndicadorBairroResponseDto::getAtivos).reversed());

        List<IndicadorTipoResponseDto> indicadoresTipo = new ArrayList<>();
        for (TipoObstaculo tipo : TipoObstaculo.values()) {
            int total = 0;
            for (Relato relato : relatos) {
                if (relato.getTipo() == tipo)
                    total++;
            }
            if (total > 0)
                indicadoresTipo.add(new IndicadorTipoResponseDto(tipo, total));
        }
        indicadoresTipo.sort(Comparator.comparingInt(IndicadorTipoResponseDto::getTotal).reversed());

        return new IndicadoresResponseDto(relatos.size(), contarAtivos(relatos),
                contarComStatus(relatos, StatusRelato.EM_OBRA), contarComStatus(relatos, StatusRelato.RESOLVIDO),
                tempoMedioResolucao(relatos), LocalDateTime.now(), indicadoresBairro, indicadoresTipo);
    }

    // Ranking dos relatos ativos, do maior para o menor Indice de Impacto. Filtros nulos sao ignorados.
    public List<PontoCriticoResponseDto> ranking(String bairro, TipoObstaculo tipo, Gravidade gravidade) throws SQLException {
        List<PontoCriticoResponseDto> pontos = new ArrayList<>();
        for (Relato relato : relatoDao.listar()) {
            boolean passaNosFiltros = (bairro == null || bairro.isBlank() || relato.getBairro().equalsIgnoreCase(bairro.trim()))
                    && (tipo == null || relato.getTipo() == tipo)
                    && (gravidade == null || relato.getGravidade() == gravidade);
            if (relato.estaAtivo() && passaNosFiltros)
                pontos.add(montarPonto(relato));
        }
        pontos.sort(Comparator.comparingInt(PontoCriticoResponseDto::getIndiceImpacto).reversed());
        for (int i = 0; i < pontos.size(); i++)
            pontos.get(i).setPosicao(i + 1);
        return pontos;
    }

    // RN19 - Indice de Impacto = 0,40 Gravidade + 0,25 Confirmacoes + 0,20 Proximidade + 0,15 Fluxo
    private PontoCriticoResponseDto montarPonto(Relato relato) {
        int maisProximo = 0;
        double menorDistancia = Double.MAX_VALUE;
        for (int i = 0; i < EQUIPAMENTOS.length; i++) {
            double distancia = GeoUtil.distanciaMetros(relato.getLatitude(), relato.getLongitude(),
                    COORDENADAS_EQUIPAMENTOS[i][0], COORDENADAS_EQUIPAMENTOS[i][1]);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProximo = i;
            }
        }

        int fatorGravidade = relato.getGravidade().getPontos();
        // 3 confirmacoes ou mais = 100
        int fatorConfirmacoes = Math.min(100, relato.getConfirmacoes() * 100 / Relato.VOTOS_PARA_MUDAR_STATUS);
        // Ate 100 m de um equipamento = 100; de 1 km em diante = 0
        int fatorProximidade = (int) Math.round(Math.max(0, Math.min(100, (1000 - menorDistancia) / 9)));
        int fatorFluxo = fluxoDoBairro(relato.getBairro());

        int indice = (int) Math.round(0.40 * fatorGravidade + 0.25 * fatorConfirmacoes
                + 0.20 * fatorProximidade + 0.15 * fatorFluxo);

        RelatoResponseDto relatoDto = mapper.map(relato, RelatoResponseDto.class);
        relatoDto.setStatus(relato.calcularStatusAtual());
        relatoDto.setConfirmacoesFaltantes(relato.calcularConfirmacoesFaltantes());

        return new PontoCriticoResponseDto(0, relatoDto, indice,
                new ComposicaoImpactoResponseDto(fatorGravidade, fatorConfirmacoes, fatorProximidade, fatorFluxo),
                EQUIPAMENTOS[maisProximo], (int) Math.round(menorDistancia));
    }

    private int fluxoDoBairro(String bairro) {
        for (int i = 0; i < BAIRROS_COM_FLUXO.length; i++) {
            if (BAIRROS_COM_FLUXO[i].equalsIgnoreCase(bairro.trim()))
                return FLUXO_DOS_BAIRROS[i];
        }
        return FLUXO_PADRAO;
    }

    private int contarAtivos(List<Relato> relatos) {
        int total = 0;
        for (Relato relato : relatos) {
            if (relato.estaAtivo())
                total++;
        }
        return total;
    }

    private int contarComStatus(List<Relato> relatos, StatusRelato status) {
        int total = 0;
        for (Relato relato : relatos) {
            if (relato.getStatus() == status)
                total++;
        }
        return total;
    }

    // Media de dias entre a criacao e a resolucao. Devolve null quando ainda nao ha relato resolvido.
    private Double tempoMedioResolucao(List<Relato> relatos) {
        long somaHoras = 0;
        int quantidade = 0;
        for (Relato relato : relatos) {
            if (relato.getStatus() == StatusRelato.RESOLVIDO && relato.getDataResolucao() != null) {
                somaHoras += Duration.between(relato.getDataCriacao(), relato.getDataResolucao()).toHours();
                quantidade++;
            }
        }
        if (quantidade == 0)
            return null;
        double mediaDias = somaHoras / 24.0 / quantidade;
        return Math.round(mediaDias * 10) / 10.0;
    }
}
