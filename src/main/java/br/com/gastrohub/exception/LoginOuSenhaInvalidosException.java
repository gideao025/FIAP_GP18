package br.com.gastrohub.exception;

public class LoginOuSenhaInvalidosException extends RuntimeException {

    public LoginOuSenhaInvalidosException() {
        super("Login ou senha inválidos");
    }
}
