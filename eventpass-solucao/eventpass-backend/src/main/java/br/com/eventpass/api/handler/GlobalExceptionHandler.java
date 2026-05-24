package br.com.eventpass.api.handler;

import br.com.eventpass.application.dto.response.ErroResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(ErroResponse.of(400, "REQUISICAO_INVALIDA", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErroResponse.of(409, "ESTADO_INVALIDO", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
            .body(ErroResponse.of(400, "VALIDACAO", erros));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex) {
        log.error("Erro inesperado: ", ex);
        return ResponseEntity.internalServerError()
            .body(ErroResponse.of(500, "ERRO_INTERNO", "Erro interno do servidor."));
    }
}
