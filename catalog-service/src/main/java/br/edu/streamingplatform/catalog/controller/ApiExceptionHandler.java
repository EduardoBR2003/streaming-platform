package br.edu.streamingplatform.catalog.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.edu.streamingplatform.catalog.dto.ErrorResponse;
import br.edu.streamingplatform.catalog.exception.ConteudoNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    // Padroniza erro 404 para todas as buscas REST de conteudo inexistente.
    @ExceptionHandler(ConteudoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ConteudoNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("not_found", exception.getMessage()));
    }

    // Junta os erros de validacao em uma resposta simples para Postman/Insomnia.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("validation_error", message));
    }
}
