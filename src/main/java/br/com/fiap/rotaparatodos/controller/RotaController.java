package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dto.AvaliacaoRotaRequestDto;
import br.com.fiap.rotaparatodos.dto.AvaliacaoRotaResponseDto;
import br.com.fiap.rotaparatodos.exception.RegraNegocioException;
import br.com.fiap.rotaparatodos.service.RotaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

// Avaliacao da acessibilidade de rotas a pe (HU03). Nao exige cadastro (RN01).
@RestController
@RequestMapping("/rotas")
public class RotaController {

    private RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    // Recebe os trajetos candidatos e devolve o indice de acessibilidade e os alertas de cada um
    @PostMapping("/avaliacoes")
    public ResponseEntity<AvaliacaoRotaResponseDto> avaliar(@Valid @RequestBody AvaliacaoRotaRequestDto dto)
            throws SQLException, RegraNegocioException {
        return ResponseEntity.ok(rotaService.avaliar(dto));
    }

}
