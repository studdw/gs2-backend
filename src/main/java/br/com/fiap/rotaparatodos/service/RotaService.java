package br.com.fiap.rotaparatodos.service;

import br.com.fiap.rotaparatodos.dao.RelatoDao;
import br.com.fiap.rotaparatodos.dto.AlertaRotaResponseDto;
import br.com.fiap.rotaparatodos.dto.AvaliacaoRotaRequestDto;
import br.com.fiap.rotaparatodos.dto.AvaliacaoRotaResponseDto;
import br.com.fiap.rotaparatodos.dto.RotaAvaliadaResponseDto;
import br.com.fiap.rotaparatodos.dto.RotaCandidataRequestDto;
import br.com.fiap.rotaparatodos.exception.RegraNegocioException;
import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.PerfilMobilidade;
import br.com.fiap.rotaparatodos.model.Relato;
import br.com.fiap.rotaparatodos.model.StatusRelato;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Calcula o indice de acessibilidade (0 a 100) das rotas a pe (HU03 e HU04).
 *
 * O trajeto pelas ruas vem do front (que consulta o OpenStreetMap). A regra de negocio fica aqui:
 * cada relato ativo perto do caminho tira pontos da rota, e o tamanho do desconto depende
 * do perfil de mobilidade de quem vai caminhar.
 */
@Service
public class RotaService {

    // Um relato conta como "no caminho" quando esta a ate 25 m da rota
    private static final double LARGURA_CORREDOR_METROS = 25;
    // Indice minimo para a rota ser considerada acessivel (RN06)
    private static final int INDICE_MINIMO_ACESSIVEL = 60;
    // Relato ainda nao confirmado pela comunidade pesa menos
    private static final double FATOR_NAO_CONFIRMADO = 0.6;
    // Teto do indice de uma rota com trecho bloqueado (RN04)
    private static final int INDICE_MAXIMO_BLOQUEADA = 20;

    private RelatoDao relatoDao;

    public RotaService(RelatoDao relatoDao) {
        this.relatoDao = relatoDao;
    }

    public AvaliacaoRotaResponseDto avaliar(AvaliacaoRotaRequestDto dto) throws SQLException, RegraNegocioException {
        int hora = dto.getHora() != null ? dto.getHora() : LocalDateTime.now().getHour();
        boolean noturno = hora >= 18 || hora < 6;

        // RN07 - somente relatos ativos (nao expirados e nao resolvidos) viram alerta
        List<Relato> relatosAtivos = new ArrayList<>();
        for (Relato relato : relatoDao.listar()) {
            if (relato.estaAtivo())
                relatosAtivos.add(relato);
        }

        List<RotaAvaliadaResponseDto> avaliadas = new ArrayList<>();
        for (int i = 0; i < dto.getRotas().size(); i++)
            avaliadas.add(avaliarRota(i, dto.getRotas().get(i), dto.getPerfil(), noturno, relatosAtivos));

        // Da mais acessivel para a menos acessivel; no empate, a mais rapida primeiro (RF07)
        avaliadas.sort(Comparator.comparingInt(RotaAvaliadaResponseDto::getIndiceAcessibilidade).reversed()
                .thenComparingInt(RotaAvaliadaResponseDto::getTempoMinutos));

        boolean existeAcessivel = false;
        for (RotaAvaliadaResponseDto rota : avaliadas) {
            if (rota.getIndiceAcessibilidade() >= INDICE_MINIMO_ACESSIVEL)
                existeAcessivel = true;
        }
        String aviso = null;
        if (!existeAcessivel)
            aviso = "Nenhuma rota atingiu um bom nível de acessibilidade para o seu perfil. "
                    + "Considere um transporte acessível ou peça apoio a alguém de confiança. "
                    + "Você ainda pode ver a melhor rota disponível e os pontos de atenção.";

        return new AvaliacaoRotaResponseDto(dto.getPerfil(), noturno, existeAcessivel, aviso, avaliadas);
    }

    private RotaAvaliadaResponseDto avaliarRota(int posicao, RotaCandidataRequestDto rota, PerfilMobilidade perfil,
                                                boolean noturno, List<Relato> relatosAtivos) throws RegraNegocioException {
        for (double[] ponto : rota.getPontos()) {
            if (ponto == null || ponto.length != 2)
                throw new RegraNegocioException("Cada ponto da rota deve ter latitude e longitude.");
        }

        List<AlertaRotaResponseDto> alertas = new ArrayList<>();
        double penalidadeTotal = 0;
        boolean bloqueada = false;

        for (Relato relato : relatosAtivos) {
            double[] proximidade = GeoUtil.proximidadeDaRota(rota.getPontos(), relato.getLatitude(), relato.getLongitude());
            if (proximidade[0] > LARGURA_CORREDOR_METROS)
                continue; // o obstaculo nao esta no caminho

            boolean confirmado = relato.getStatus() == StatusRelato.CONFIRMADO
                    || relato.getStatus() == StatusRelato.EM_OBRA;
            // RN02 - o peso de cada obstaculo depende do perfil (matriz de pesos no enum PerfilMobilidade)
            double penalidade = perfil.pesoDe(relato.getTipo()) * relato.getGravidade().getFator();
            if (!confirmado)
                penalidade = penalidade * FATOR_NAO_CONFIRMADO;
            // RN05 - a noite, iluminacao ruim e local isolado valem o dobro
            if (noturno && (relato.getTipo() == TipoObstaculo.ILUMINACAO_RUIM
                    || relato.getTipo() == TipoObstaculo.LOCAL_ISOLADO))
                penalidade = penalidade * 2;
            // RN04 - critico confirmado bloqueia o trecho para cadeira de rodas e carrinho de bebe
            boolean bloqueia = confirmado && relato.getGravidade() == Gravidade.CRITICA
                    && (perfil == PerfilMobilidade.CADEIRANTE || perfil == PerfilMobilidade.CARRINHO_BEBE);

            penalidadeTotal += penalidade;
            if (bloqueia)
                bloqueada = true;
            alertas.add(new AlertaRotaResponseDto(relato.getId(), relato.getTipo(), relato.getGravidade(),
                    relato.getStatus(), relato.getEndereco(), relato.getLatitude(), relato.getLongitude(),
                    (int) Math.round(proximidade[1]), (int) Math.round(penalidade), bloqueia));
        }

        int indice = (int) Math.max(0, Math.round(100 - penalidadeTotal));
        if (bloqueada)
            indice = Math.min(indice, INDICE_MAXIMO_BLOQUEADA);
        alertas.sort(Comparator.comparingInt(AlertaRotaResponseDto::getDistanciaDaOrigemMetros));

        int tempoMinutos = (int) Math.ceil(rota.getDistanciaMetros() / perfil.getVelocidadeMetrosPorSegundo() / 60);
        return new RotaAvaliadaResponseDto(posicao, (int) Math.round(rota.getDistanciaMetros()), tempoMinutos, indice,
                classificar(indice, bloqueada), bloqueada, alertas);
    }

    private String classificar(int indice, boolean bloqueada) {
        if (bloqueada)
            return "Trecho bloqueado";
        if (indice >= 80)
            return "Muito acessível";
        if (indice >= INDICE_MINIMO_ACESSIVEL)
            return "Acessível";
        if (indice >= 40)
            return "Pouco acessível";
        return "Não recomendada";
    }
}
