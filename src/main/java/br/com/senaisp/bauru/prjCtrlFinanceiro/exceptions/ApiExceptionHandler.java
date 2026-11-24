package br.com.senaisp.bauru.prjCtrlFinanceiro.exceptions;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 404 - usuário, categoria ou lançamento inexistente
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    // 400 - regra de negócio / validação
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> requisicaoInvalida(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    // 400 - JSON malformado, data ou número em formato inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> corpoInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("erro", "Corpo da requisição inválido (verifique JSON, datas e valores)"));
    }

    // 400 - id da rota que não é número
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("erro", "Parâmetro inválido: " + ex.getName()));
    }

    // 409 - conflito com restrições do banco
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflito(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409)
                .body(Map.of("erro", "Operação viola uma restrição de integridade dos dados"));
    }

    // 401 - login com e-mail/senha inválidos (mensagem genérica de propósito).
    // Token ausente/inválido/expirado NÃO passa por aqui: o filtro do Spring Security responde antes do controller.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> credenciaisInvalidas(BadCredentialsException ex) {
        return ResponseEntity.status(401)
                .body(Map.of("erro", "Credenciais inválidas"));
    }
}
