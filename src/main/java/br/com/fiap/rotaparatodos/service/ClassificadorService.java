package br.com.fiap.rotaparatodos.service;

import br.com.fiap.rotaparatodos.dto.ClassificacaoResponseDto;
import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

/**
 * Classificacao automatica do relato (HU07).
 *
 * IMPORTANTE: nesta versao a "IA" e SIMULADA. Em vez de um modelo de visao analisando a foto,
 * a sugestao sai de palavras-chave da descricao escrita pelo cidadao. O resultado (tipo, gravidade
 * e confianca) e o mesmo previsto no projeto, entao da para trocar esta classe por um modelo real
 * (ou pela API Flask da disciplina de IA) sem mexer no resto do sistema.
 */
@Service
public class ClassificadorService {

    // Abaixo desta confianca o relato vai para moderacao humana (RN11)
    public static final int CONFIANCA_MINIMA = 70;

    // A ordem importa: o primeiro tipo que bater e o escolhido. Palavras sem acento (o texto e normalizado).
    private static final TipoObstaculo[] TIPOS = {
            TipoObstaculo.BURACO, TipoObstaculo.SEM_RAMPA, TipoObstaculo.DEGRAU, TipoObstaculo.ILUMINACAO_RUIM,
            TipoObstaculo.LOCAL_ISOLADO, TipoObstaculo.TRAVESSIA_INSEGURA, TipoObstaculo.OBSTRUCAO,
            TipoObstaculo.CALCADA_IRREGULAR};
    private static final String[][] PALAVRAS_DOS_TIPOS = {
            {"buraco", "cratera", "afund"},
            {"sem rampa", "falta rampa", "falta de rampa", "nao tem rampa", "guia alta", "guia sem rebaix", "rebaixamento"},
            {"degrau", "escada", "desnivel"},
            {"escur", "iluminacao", "sem luz", "poste apagado", "lampada"},
            {"isolad", "desert", "ermo", "abandonad"},
            {"travessia", "semaforo", "faixa de pedestre", "atravessar", "cruzamento"},
            {"obstru", "entulho", "lixo", "carro na calcada", "estacionad", "mesa", "tapume", "bloquead", "arvore caida", "poste no meio"},
            {"irregular", "quebrad", "raiz", "solto", "rachad", "piso", "pedra", "esburacad"}};

    private static final Gravidade[] GRAVIDADES = {Gravidade.CRITICA, Gravidade.ALTA, Gravidade.BAIXA};
    private static final String[][] PALAVRAS_DAS_GRAVIDADES = {
            {"impossivel", "nao passa", "nao da para passar", "intransponivel", "bloqueia", "cai ", "queda", "acidente", "critico"},
            {"grande", "fundo", "perigos", "risco", "muito", "alto", "grave", "enorme"},
            {"pequen", "leve", "pouco", "baixo"}};

    public ClassificacaoResponseDto classificar(String descricao) {
        String texto = normalizar(descricao);

        // Sem descricao nao ha no que se basear: sugestao generica e confianca baixa
        if (texto.isBlank())
            return montar(TipoObstaculo.CALCADA_IRREGULAR, Gravidade.MEDIA, 40);

        TipoObstaculo tipo = null;
        for (int i = 0; i < TIPOS.length && tipo == null; i++) {
            if (contemAlguma(texto, PALAVRAS_DOS_TIPOS[i]))
                tipo = TIPOS[i];
        }

        Gravidade gravidade = null;
        for (int i = 0; i < GRAVIDADES.length && gravidade == null; i++) {
            if (contemAlguma(texto, PALAVRAS_DAS_GRAVIDADES[i]))
                gravidade = GRAVIDADES[i];
        }

        // Quanto mais pistas o texto da, maior a confianca da sugestao
        int confianca;
        if (tipo != null && gravidade != null)
            confianca = 92;
        else if (tipo != null)
            confianca = 80;
        else if (gravidade != null)
            confianca = 58;
        else
            confianca = 45;

        if (tipo == null)
            tipo = TipoObstaculo.CALCADA_IRREGULAR;
        if (gravidade == null)
            gravidade = Gravidade.MEDIA;
        return montar(tipo, gravidade, confianca);
    }

    private ClassificacaoResponseDto montar(TipoObstaculo tipo, Gravidade gravidade, int confianca) {
        return new ClassificacaoResponseDto(tipo, gravidade, confianca, confianca < CONFIANCA_MINIMA);
    }

    private boolean contemAlguma(String texto, String[] palavras) {
        for (String palavra : palavras) {
            if (texto.contains(palavra))
                return true;
        }
        return false;
    }

    // Minusculas e sem acentos, para "Iluminação" e "iluminacao" serem a mesma coisa
    private String normalizar(String texto) {
        if (texto == null)
            return "";
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().trim() + " ";
    }
}
