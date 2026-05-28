package br.edu.streamingplatform.catalog.exception;

public class ConteudoNotFoundException extends RuntimeException {

    // Cria uma mensagem clara quando alguem consulta um ID inexistente.
    public ConteudoNotFoundException(Long id) {
        super("Content not found with id " + id);
    }
}
