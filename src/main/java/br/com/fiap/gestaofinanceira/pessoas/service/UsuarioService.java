package br.com.fiap.gestaofinanceira.pessoas.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.pessoas.entities.Usuario;
import br.com.fiap.gestaofinanceira.pessoas.exception.PessoaException;
import br.com.fiap.gestaofinanceira.pessoas.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        // Regra 1: nao cadastrar CPF repetido
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new PessoaException("Já existe um usuário cadastrado com o CPF " + usuario.getCpf());
        }
        usuario.setId(null); // o id e sempre gerado pela aplicacao
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Contrato com os outros modulos: lanca 404 se o usuario nao existir.
     * Ex.: usuarioService.buscarPorId(divida.getIdPessoa());
     */
    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o id " + id));
    }

    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o CPF " + cpf));
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Usuario atualizar(String id, Usuario dados) {
        Usuario usuario = buscarPorId(id);

        // Regra 1 na atualizacao: o novo CPF nao pode ser de outro usuario
        if (usuarioRepository.existsByCpfAndIdNot(dados.getCpf(), id)) {
            throw new PessoaException("O CPF " + dados.getCpf() + " já pertence a outro usuário");
        }

        usuario.setNome(dados.getNome());
        usuario.setCpf(dados.getCpf());
        usuario.setEmail(dados.getEmail());
        usuario.setTelefone(dados.getTelefone());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void remover(String id) {
        Usuario usuario = buscarPorId(id);

        // Regra 2: nao remover usuario que ainda tem enderecos cadastrados
        if (!usuario.getEnderecos().isEmpty()) {
            throw new PessoaException("O usuário possui " + usuario.getEnderecos().size()
                    + " endereço(s) cadastrado(s). Remova os endereços antes de remover o usuário");
        }
        try {
            usuarioRepository.delete(usuario);
            usuarioRepository.flush(); // forca o DELETE agora para cair no catch
        } catch (DataIntegrityViolationException e) {
            // O usuario ainda e referenciado por outro modulo (conta, divida, notificacao...)
            throw new PessoaException("O usuário não pode ser removido porque possui registros "
                    + "vinculados em outros módulos (contas, dívidas, notificações...)", e);
        }
    }
}
