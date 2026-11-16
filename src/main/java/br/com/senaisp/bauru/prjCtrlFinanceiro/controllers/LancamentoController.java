package br.com.senaisp.bauru.prjCtrlFinanceiro.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

// O servlet path já é /api (application.properties), então não repetimos /api aqui.
// ATENÇÃO: usuarioId na rota é DIDÁTICO. Não é autenticação nem isolamento de segurança.
// Com JWT, ele passará a vir do contexto de segurança.
@RestController
@RequestMapping("/usuarios/{usuarioId}/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<LancamentoResponse> listar(@PathVariable Long usuarioId) {
        return service.listar(usuarioId);
    }

    @GetMapping("/{id}")
    public LancamentoResponse buscarPorId(@PathVariable Long usuarioId,
                                          @PathVariable Long id) {
        return service.buscarPorId(usuarioId, id);
    }

    @PostMapping
    public ResponseEntity<LancamentoResponse> cadastrar(
            @PathVariable Long usuarioId, @RequestBody LancamentoRequest dados) {
        return ResponseEntity.status(201).body(service.cadastrar(usuarioId, dados));
    }

    @PutMapping("/{id}")
    public LancamentoResponse atualizar(@PathVariable Long usuarioId,
                                        @PathVariable Long id,
                                        @RequestBody LancamentoRequest dados) {
        return service.atualizar(usuarioId, id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long usuarioId,
                                        @PathVariable Long id) {
        service.excluir(usuarioId, id);
        return ResponseEntity.noContent().build();
    }
}
