package br.com.fiap.rotaparatodos.controller;

import br.com.fiap.rotaparatodos.dao.UsuarioDao;
import br.com.fiap.rotaparatodos.dto.LoginRequestDto;
import br.com.fiap.rotaparatodos.dto.LoginResponseDto;
import br.com.fiap.rotaparatodos.dto.UsuarioResponseDto;
import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.exception.NaoAutenticadoException;
import br.com.fiap.rotaparatodos.model.Usuario;
import br.com.fiap.rotaparatodos.service.TokenService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UsuarioDao dao;
    private TokenService tokenService;
    private BCryptPasswordEncoder encoder;
    private ModelMapper mapper;

    public AuthController(UsuarioDao dao, TokenService tokenService, BCryptPasswordEncoder encoder, ModelMapper mapper) {
        this.dao = dao;
        this.tokenService = tokenService;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    // 200 com o token; 400 se faltar campo; 401 se o e-mail ou a senha estiverem errados
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> entrar(@Valid @RequestBody LoginRequestDto dto)
            throws SQLException, NaoAutenticadoException {
        Usuario usuario;
        try {
            usuario = dao.buscarPorEmail(dto.getEmail().trim());
        } catch (EntidadeNaoEncontradaException e) {
            // A mesma mensagem para e-mail ou senha errados: assim nao revelamos quais e-mails existem
            throw new NaoAutenticadoException("E-mail ou senha incorretos.");
        }
        if (!encoder.matches(dto.getSenha(), usuario.getSenha()))
            throw new NaoAutenticadoException("E-mail ou senha incorretos.");

        String token = tokenService.gerar(usuario.getId());
        return ResponseEntity.ok(new LoginResponseDto(token, mapper.map(usuario, UsuarioResponseDto.class)));
    }

}
