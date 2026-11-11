package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dto.IndicadoresResponseDto;
import br.com.fiap.rotaparatodos.dto.PontoCriticoResponseDto;
import br.com.fiap.rotaparatodos.model.Gravidade;
import br.com.fiap.rotaparatodos.model.TipoObstaculo;
import br.com.fiap.rotaparatodos.service.PainelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

// Painel publico e ranking de pontos criticos. Consulta aberta, sem login (RN01).
@RestController
@RequestMapping("/painel")
public class PainelController {

    private PainelService painelService;

    public PainelController(PainelService painelService) {
        this.painelService = painelService;
    }

    // Indicadores gerais, por bairro e por tipo de obstaculo (HU13)
    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresResponseDto> indicadores() throws SQLException {
        return ResponseEntity.ok(painelService.indicadores());
    }

    // Ranking pelo Indice de Impacto, com filtros opcionais (HU11)
    @GetMapping("/ranking")
    public List<PontoCriticoResponseDto> ranking(@RequestParam(required = false) String bairro,
                                                 @RequestParam(required = false) TipoObstaculo tipo,
                                                 @RequestParam(required = false) Gravidade gravidade) throws SQLException {
        return painelService.ranking(bairro, tipo, gravidade);
    }

}
