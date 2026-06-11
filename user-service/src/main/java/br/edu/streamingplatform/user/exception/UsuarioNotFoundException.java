package br.edu.streamingplatform.user.exception;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long id) {
        super("User not found with id " + id);
    }
}
