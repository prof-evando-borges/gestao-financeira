package br.com.fiap.gestaofinanceira.contas.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.contas.entity.Banco;
import br.com.fiap.gestaofinanceira.contas.exception.ContaException;
import br.com.fiap.gestaofinanceira.contas.repository.BancoRepository;
import br.com.fiap.gestaofinanceira.contas.repository.ContaBancariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BancoService {

    private final BancoRepository bancoRepository;
    private final ContaBancariaRepository contaBancariaRepository;

    public BancoService(BancoRepository bancoRepository, ContaBancariaRepository contaBancariaRepository) {
        this.bancoRepository = bancoRepository;
        this.contaBancariaRepository = contaBancariaRepository;
    }

    @Transactional
    public Banco cadastrar(Banco banco) {
        // Regra: o codigo COMPE identifica o banco, entao nao pode repetir
        if (bancoRepository.existsByCodigo(banco.getCodigo())) {
            throw new ContaException("Já existe um banco cadastrado com o código " + banco.getCodigo());
        }
        banco.setId(null);
        return bancoRepository.save(banco);
    }

    public List<Banco> listarTodos() {
        return bancoRepository.findAll();
    }

    public List<Banco> listar(String codigo) {
        if (codigo == null) {
            return listarTodos();
        }
        return bancoRepository.findByCodigo(codigo);
    }

    public Banco buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id do banco");
        }
        return bancoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Banco não encontrado: " + id));
    }

    @Transactional
    public Banco atualizar(String id, Banco dados) {
        Banco banco = buscarPorId(id);
        if (bancoRepository.existsByCodigoAndIdNot(dados.getCodigo(), id)) {
            throw new ContaException("Já existe outro banco cadastrado com o código " + dados.getCodigo());
        }
        banco.setCodigo(dados.getCodigo());
        banco.setNome(dados.getNome());
        return bancoRepository.save(banco);
    }

    @Transactional
    public void remover(String id) {
        Banco banco = buscarPorId(id);
        // Regra: banco com contas vinculadas nao pode ser removido
        if (contaBancariaRepository.existsByBancoId(id)) {
            throw new ContaException("O banco " + banco.getNome() + " possui contas vinculadas e não pode ser removido");
        }
        bancoRepository.delete(banco);
    }
}
