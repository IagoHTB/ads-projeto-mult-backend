package br.com.senaisp.bauru.prjCtrlFinanceiro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.senaisp.bauru.prjCtrlFinanceiro.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
