package br.com.gastrohub.exception;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException() {
        super("Acesso negado: você não tem permissão para realizar esta operação");
    }
}
