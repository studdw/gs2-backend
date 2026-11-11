package br.com.fiap.rotaparatodos.handler;

import br.com.fiap.rotaparatodos.dto.ErroResponseDto;
import br.com.fiap.rotaparatodos.exception.AcessoNegadoException;
import br.com.fiap.rotaparatodos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.rotaparatodos.exception.NaoAutenticadoException;
import br.com.fiap.rotaparatodos.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Transforma as excecoes em respostas JSON padronizadas, sem stack trace e sem detalhes do SQL
@RestControllerAdvice
public class ApiExceptionHandler {

    // Registra o erro no console do servidor (para o grupo investigar), sem mostrar ao usuario
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // 400 - campos do DTO que nao passaram no Bean Validation: { "campo": "mensagem" }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e){
        Map<String, String> erros = new HashMap<>();
        for (FieldError field : e.getFieldErrors())
            erros.put(field.getField(), field.getDefaultMessage());
        return ResponseEntity.badRequest().body(erros);
    }

    // 400 - JSON mal escrito ou valor fora das opcoes aceitas (ex.: tipo de obstaculo que nao existe)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDto> jsonInvalido(HttpMessageNotReadableException e){
        return ResponseEntity.badRequest().body(
                new ErroResponseDto("Bad Request", "Não foi possível ler os dados enviados. Confira o JSON e os valores."));
    }

    // 400 - parametro da URL com tipo errado (ex.: /relatos/abc) ou parametro obrigatorio ausente
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResponseDto> parametroInvalido(Exception e){
        return ResponseEntity.badRequest().body(
                new ErroResponseDto("Bad Request", "Parâmetro inválido ou ausente na requisição."));
    }

    // 401 - precisa estar logado
    @ExceptionHandler(NaoAutenticadoException.class)
    public ResponseEntity<ErroResponseDto> naoAutenticado(NaoAutenticadoException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErroResponseDto("Unauthorized", e.getMessage()));
    }

    // 403 - logado, mas sem permissao
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponseDto> acessoNegado(AcessoNegadoException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErroResponseDto("Forbidden", e.getMessage()));
    }

    // 404 - registro nao encontrado
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponseDto> notFound(EntidadeNaoEncontradaException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErroResponseDto("Not Found", e.getMessage()));
    }

    // 404 - endereco que nao existe na API
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDto> rotaInexistente(NoResourceFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErroResponseDto("Not Found", "Este endereço não existe na API."));
    }

    // 405 - metodo HTTP nao aceito no endereco
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponseDto> metodoNaoSuportado(HttpRequestMethodNotSupportedException e){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErroResponseDto("Method Not Allowed", "O método " + e.getMethod() + " não é aceito neste endereço."));
    }

    // 415 - corpo enviado sem o cabecalho Content-Type: application/json
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResponseDto> tipoNaoSuportado(HttpMediaTypeNotSupportedException e){
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                new ErroResponseDto("Unsupported Media Type", "Envie os dados como application/json."));
    }

    // 409 - regra de negocio violada (todas as regras sao filhas de RegraNegocioException)
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDto> regraNegocio(RegraNegocioException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErroResponseDto("Conflict", e.getMessage()));
    }

    // 500 - erro no banco. A mensagem do SQL fica so no log: a rubrica proibe devolver detalhes do SQL.
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResponseDto> serverInternalError(SQLException e){
        log.error("Erro no banco de dados", e);
        return ResponseEntity.internalServerError().body(
                new ErroResponseDto("Internal Server Error", "Não foi possível acessar os dados agora. Tente de novo em instantes."));
    }

    // 500 - qualquer outro erro inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDto> erroInesperado(Exception e){
        log.error("Erro inesperado", e);
        return ResponseEntity.internalServerError().body(
                new ErroResponseDto("Internal Server Error", "Ocorreu um erro inesperado. Tente de novo em instantes."));
    }

}
