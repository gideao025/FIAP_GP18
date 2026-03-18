package org.gideao.pocuser.controller;

import java.time.OffsetDateTime;
import java.util.List;
import org.gideao.pocuser.dto.ErrorResponseDto;
import org.gideao.pocuser.exception.CredenciaisInvalidasException;
import org.gideao.pocuser.exception.EmailJaCadastradoException;
import org.gideao.pocuser.exception.LoginJaCadastradoException;
import org.gideao.pocuser.exception.UsuarioNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, details);
    }

    @ExceptionHandler({
            EmailJaCadastradoException.class,
            LoginJaCadastradoException.class
    })
    public ResponseEntity<ErrorResponseDto> handleConflict(RuntimeException ex) {
        return buildResponse(HttpStatus.CONFLICT, List.of(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(UsuarioNaoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(CredenciaisInvalidasException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, List.of(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(HttpStatus status, List<String> details) {
        ErrorResponseDto response = new ErrorResponseDto(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
