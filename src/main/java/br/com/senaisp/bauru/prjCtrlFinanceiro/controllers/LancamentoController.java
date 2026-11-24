package br.com.senaisp.bauru.prjCtrlFinanceiro.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.senaisp.bauru.prjCtrlFinanceiro.dto.LancamentoRequest;
import br.com.senaisp.bauru.prjCtrlFinanceiro.dto.LancamentoResponse;
import br.com.senaisp.bauru.prjCtrlFinanceiro.services.LancamentoService;

// O dono do lançamento vem SEMPRE do token (jwt.getSubject()), nunca da URL.
@RestController
@RequestMapping("/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    @GetMapping
    public List<LancamentoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return service.listar(usuarioId(jwt));
    }

    @GetMapping("/{id}")
    public LancamentoResponse buscarPorId(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable Long id) {
        return service.buscarPorId(usuarioId(jwt), id);
    }

    @PostMapping
    public ResponseEntity<LancamentoResponse> cadastrar(
            @AuthenticationPrincipal Jwt jwt, @RequestBody LancamentoRequest dados) {
        return ResponseEntity.status(201).body(service.cadastrar(usuarioId(jwt), dados));
    }

    @PutMapping("/{id}")
    public LancamentoResponse atualizar(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable Long id,
                                        @RequestBody LancamentoRequest dados) {
        return service.atualizar(usuarioId(jwt), id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable Long id) {
        service.excluir(usuarioId(jwt), id);
        return ResponseEntity.noContent().build();
    }
}
