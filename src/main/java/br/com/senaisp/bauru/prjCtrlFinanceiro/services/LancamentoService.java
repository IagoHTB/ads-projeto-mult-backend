package br.com.senaisp.bauru.prjCtrlFinanceiro.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.senaisp.bauru.prjCtrlFinanceiro.dto.LancamentoRequest;
import br.com.senaisp.bauru.prjCtrlFinanceiro.dto.LancamentoResponse;
import br.com.senaisp.bauru.prjCtrlFinanceiro.exceptions.RecursoNaoEncontradoException;
import br.com.senaisp.bauru.prjCtrlFinanceiro.models.Categoria;
import br.com.senaisp.bauru.prjCtrlFinanceiro.models.Lancamento;
import br.com.senaisp.bauru.prjCtrlFinanceiro.models.Usuario;
import br.com.senaisp.bauru.prjCtrlFinanceiro.repositories.CategoriaRepository;
import br.com.senaisp.bauru.prjCtrlFinanceiro.repositories.LancamentoRepository;
import br.com.senaisp.bauru.prjCtrlFinanceiro.repositories.UsuarioRepository;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentos;
    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;

    public LancamentoService(LancamentoRepository lancamentos,
            UsuarioRepository usuarios, CategoriaRepository categorias) {
        this.lancamentos = lancamentos;
        this.usuarios = usuarios;
        this.categorias = categorias;
    }

    @Transactional
    public LancamentoResponse cadastrar(Long usuarioId, LancamentoRequest dados) {
        validar(dados);

        Usuario usuario = usuarios.findById(usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        Categoria categoria = buscarCategoria(dados.categoriaId());
        exigirCategoriaAtiva(categoria);

        Lancamento l = new Lancamento();
        l.setUsuario(usuario);
        preencher(l, dados, categoria);
        return converter(lancamentos.save(l));
    }

    @Transactional(readOnly = true)
    public List<LancamentoResponse> listar(Long usuarioId) {
        if (!usuarios.existsById(usuarioId)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }
        return lancamentos.findByUsuarioId(usuarioId).stream()
            .map(this::converter).toList();
    }

    @Transactional(readOnly = true)
    public LancamentoResponse buscarPorId(Long usuarioId, Long id) {
        return converter(buscarDoUsuario(usuarioId, id));
    }

    @Transactional
    public LancamentoResponse atualizar(Long usuarioId, Long id, LancamentoRequest dados) {
        validar(dados);

        Lancamento l = buscarDoUsuario(usuarioId, id);
        Categoria categoria = buscarCategoria(dados.categoriaId());

        // Só exige categoria ativa se a categoria foi trocada:
        // um lançamento histórico pode continuar numa categoria inativada depois.
        if (!categoria.getId().equals(l.getCategoria().getId())) {
            exigirCategoriaAtiva(categoria);
        }

        // O proprietário (usuario) nunca é alterado no PUT.
        preencher(l, dados, categoria);
        return converter(lancamentos.save(l));
    }

    @Transactional
    public void excluir(Long usuarioId, Long id) {
        Lancamento l = buscarDoUsuario(usuarioId, id);
        lancamentos.delete(l);
    }

    // ---------- métodos auxiliares ----------

    private void validar(LancamentoRequest dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados obrigatórios inválidos");
        }
        if (dados.descricao() == null) {
            throw new IllegalArgumentException("A descrição é obrigatória");
        }
        int tamanho = dados.descricao().trim().length();
        if (tamanho < 3 || tamanho > 200) {
            throw new IllegalArgumentException("A descrição deve ter entre 3 e 200 caracteres");
        }
        if (dados.valor() == null || dados.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        if (dados.dataLancamento() == null) {
            throw new IllegalArgumentException("A data do lançamento é obrigatória");
        }
        if (dados.categoriaId() == null) {
            throw new IllegalArgumentException("A categoria é obrigatória");
        }
        if (dados.observacao() != null && dados.observacao().length() > 500) {
            throw new IllegalArgumentException("A observação deve ter no máximo 500 caracteres");
        }
    }

    private Categoria buscarCategoria(Long categoriaId) {
        return categorias.findById(categoriaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));
    }

    private void exigirCategoriaAtiva(Categoria categoria) {
        if (!Boolean.TRUE.equals(categoria.getAtiva())) {
            throw new IllegalArgumentException("Categoria inativa!");
        }
    }

    // Toda consulta filtra por usuario_id
    private Lancamento buscarDoUsuario(Long usuarioId, Long id) {
        return lancamentos.findByIdAndUsuarioId(id, usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado"));
    }

    private void preencher(Lancamento l, LancamentoRequest dados, Categoria categoria) {
        l.setCategoria(categoria);
        l.setDescricao(dados.descricao().trim());
        l.setValor(dados.valor());
        l.setDataLancamento(dados.dataLancamento());
        l.setObservacao(dados.observacao());
    }

    private LancamentoResponse converter(Lancamento l) {
        return new LancamentoResponse(l.getId(), l.getDescricao(),
            l.getValor(), l.getDataLancamento(), l.getObservacao(),
            l.getCategoria().getId(), l.getCategoria().getDescricao(),
            l.getUsuario().getId());
    }
}
