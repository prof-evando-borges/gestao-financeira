package br.com.fiap.gestaofinanceira.planejamentos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "br.com.fiap.gestaofinanceira.planejamentos")
public class PlanejamentoExceptionHandler {

    @ExceptionHandler(PlanejamentoException.class)
    public ResponseEntity<Map<String, Object>> tratarPlanejamentoException(PlanejamentoException ex) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        corpo.put("erro", "Regra de negócio violada");
        corpo.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(corpo);
    }
}