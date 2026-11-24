package br.com.senaisp.bauru.prjCtrlFinanceiro.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.senaisp.bauru.prjCtrlFinanceiro.dto.LoginRequest;
import br.com.senaisp.bauru.prjCtrlFinanceiro.dto.LoginResponse;
import br.com.senaisp.bauru.prjCtrlFinanceiro.services.AutenticacaoService;

// Servlet path já é /api, então a URL pública é POST /api/auth/login
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticacaoService service;

    public AuthController(AutenticacaoService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }
}
