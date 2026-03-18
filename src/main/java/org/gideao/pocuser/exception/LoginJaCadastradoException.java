package org.gideao.pocuser.exception;

public class LoginJaCadastradoException extends RuntimeException {

    public LoginJaCadastradoException(String login) {
        super("O login já está cadastrado: " + login);
    }
}
