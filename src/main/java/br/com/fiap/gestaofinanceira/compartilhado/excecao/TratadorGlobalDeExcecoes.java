package br.com.fiap.gestaofinanceira.compartilhado.excecao;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converte as exceções da aplicação em respostas HTTP padronizadas (RFC 9457 - Problem Details).
 * Vale para os controllers de todos os módulos; os grupos não precisam criar o próprio tratador.
 */
@RestControllerAdvice
public class TratadorGlobalDeExcecoes {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail tratarRegraDeNegocio(RegraDeNegocioException ex) {
        // 422 Unprocessable Content
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> erros.putIfAbsent(erro.getField(), erro.getDefaultMessage()));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos.");
        problema.setProperty("campos", erros);
        return problema;
    }

    // @Pattern, @Size, @Min... em @PathVariable e @RequestParam
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail tratarValidacaoDeParametro(HandlerMethodValidationException ex) {
        List<String> erros = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Um ou mais parâmetros são inválidos.");
        problema.setProperty("erros", erros);
        return problema;
    }

    // Ex.: ?status=FECHADA quando o enum so aceita ATIVA, BLOQUEADA ou ENCERRADA
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tratarParametroComTipoErrado(MethodArgumentTypeMismatchException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + ex.getName() + "': " + ex.getValue());
    }

    // JSON malformado, data em formato errado ou valor fora da lista de um enum no corpo
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "O corpo da requisição é inválido: confira o JSON, as datas (aaaa-mm-dd) e os valores dos enums.");
    }

    // Restricao do banco (UNIQUE, FK, CHECK) que o Service nao conferiu antes
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail tratarViolacaoNoBanco(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "A operação viola uma restrição do banco de dados (registro repetido ou vinculado a outro módulo).");
    }
}
