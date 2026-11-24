package br.com.senaisp.bauru.prjCtrlFinanceiro.dto;

public record LoginResponse(String token, String tipo, long expiraEmSegundos) {}
