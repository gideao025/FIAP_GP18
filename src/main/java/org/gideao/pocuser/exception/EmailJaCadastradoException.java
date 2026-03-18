package org.gideao.pocuser.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("O email já está cadastrado: " + email);
    }
}
