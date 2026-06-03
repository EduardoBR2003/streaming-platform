package br.edu.streamingplatform.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("User already exists with email " + email);
    }
}
