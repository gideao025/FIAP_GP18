package br.com.gastrohub.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ProblemDetail> tratarAcessoNegado(AcessoNegadoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/acesso-negado"));
        problemDetail.setTitle("Acesso negado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> tratarUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/usuario-nao-encontrado"));
        problemDetail.setTitle("Usuário não encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(LoginOuSenhaInvalidosException.class)
    public ResponseEntity<ProblemDetail> tratarLoginOuSenhaInvalidos(LoginOuSenhaInvalidosException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/credenciais-invalidas"));
        problemDetail.setTitle("Credenciais inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(SenhaAtualInvalidaException.class)
    public ResponseEntity<ProblemDetail> tratarSenhaAtualInvalida(SenhaAtualInvalidaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/senha-atual-invalida"));
        problemDetail.setTitle("Senha atual incorreta");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    public ResponseEntity<ProblemDetail> tratarOperacaoNaoPermitida(OperacaoNaoPermitidaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/operacao-nao-permitida"));
        problemDetail.setTitle("Operação não permitida");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(DadosJaCadastradosException.class)
    public ResponseEntity<ProblemDetail> tratarDadosJaCadastrados(DadosJaCadastradosException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/dados-duplicados"));
        problemDetail.setTitle("Dados já cadastrados");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> tratarErrosDeValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> errosPorCampo = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(erro -> {
            String campo = ((FieldError) erro).getField();
            String mensagem = erro.getDefaultMessage();
            errosPorCampo.put(campo, mensagem);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Erro de validação nos campos"
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/validacao"));
        problemDetail.setTitle("Erro de validação");
        problemDetail.setProperty("erros", errosPorCampo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> tratarErroGenerico(Exception ex) {
        log.error("Erro interno não tratado", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor"
        );
        problemDetail.setType(URI.create("https://api.gastrohub.com/errors/erro-interno"));
        problemDetail.setTitle("Erro interno do servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
