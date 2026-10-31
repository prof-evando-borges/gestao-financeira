package br.com.fiap.gestaofinanceira.pessoas.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.pessoas.entities.Endereco;
import br.com.fiap.gestaofinanceira.pessoas.entities.Usuario;
import br.com.fiap.gestaofinanceira.pessoas.exception.PessoaException;
import br.com.fiap.gestaofinanceira.pessoas.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class EnderecoService {

    private static final int LIMITE_ENDERECOS_POR_USUARIO = 5;

    private static final Set<String> UFS_VALIDAS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    private final EnderecoRepository enderecoRepository;
    private final UsuarioService usuarioService;

    public EnderecoService(EnderecoRepository enderecoRepository, UsuarioService usuarioService) {
        this.enderecoRepository = enderecoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Endereco cadastrar(Endereco endereco) {
        Usuario usuario = buscarUsuarioDoEndereco(endereco);
        String uf = validarUf(endereco.getEstado());

        // Regra 3: limite de enderecos por usuario
        if (enderecoRepository.countByUsuarioId(usuario.getId()) >= LIMITE_ENDERECOS_POR_USUARIO) {
            throw new PessoaException("O usuário já possui o limite de "
                    + LIMITE_ENDERECOS_POR_USUARIO + " endereços cadastrados");
        }

        // Regra 4: nao duplicar endereco (mesmo CEP e numero) para o mesmo usuario
        if (enderecoRepository.existsByUsuarioIdAndCepAndNumero(
                usuario.getId(), endereco.getCep(), endereco.getNumero())) {
            throw new PessoaException("O usuário já possui um endereço com o CEP "
                    + endereco.getCep() + " e número " + endereco.getNumero());
        }

        endereco.setId(null);
        endereco.setEstado(uf);
        endereco.setUsuario(usuario);
        return enderecoRepository.save(endereco);
    }

    public List<Endereco> listarTodos() {
        return enderecoRepository.findAll();
    }

    public Endereco buscarPorId(String id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado com o id " + id));
    }

    public List<Endereco> listarPorUsuario(String idUsuario) {
        usuarioService.buscarPorId(idUsuario); // 404 se o usuario nao existir
        return enderecoRepository.findByUsuarioId(idUsuario);
    }

    @Transactional
    public Endereco atualizar(String id, Endereco dados) {
        Endereco endereco = buscarPorId(id);
        Usuario usuario = buscarUsuarioDoEndereco(dados);
        String uf = validarUf(dados.getEstado());

        if (enderecoRepository.existsByUsuarioIdAndCepAndNumeroAndIdNot(
                usuario.getId(), dados.getCep(), dados.getNumero(), id)) {
            throw new PessoaException("O usuário já possui um endereço com o CEP "
                    + dados.getCep() + " e número " + dados.getNumero());
        }

        endereco.setUsuario(usuario);
        endereco.setCep(dados.getCep());
        endereco.setLogradouro(dados.getLogradouro());
        endereco.setNumero(dados.getNumero());
        endereco.setComplemento(dados.getComplemento());
        endereco.setBairro(dados.getBairro());
        endereco.setCidade(dados.getCidade());
        endereco.setEstado(uf);
        return enderecoRepository.save(endereco);
    }

    @Transactional
    public void remover(String id) {
        Endereco endereco = buscarPorId(id);
        // Tira da lista do usuario para o orphanRemoval nao se confundir
        endereco.getUsuario().getEnderecos().remove(endereco);
        enderecoRepository.delete(endereco);
    }

    // Busca o usuario pelo id enviado ("usuario": { "id": "..." }); 404 se nao existir
    private Usuario buscarUsuarioDoEndereco(Endereco endereco) {
        String idUsuario = endereco.getUsuario().getId();
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new PessoaException("Informe o id do usuário do endereço: \"usuario\": { \"id\": \"...\" }");
        }
        return usuarioService.buscarPorId(idUsuario);
    }

    // Regra 5: o estado tem que ser uma UF brasileira existente
    private String validarUf(String estado) {
        String uf = estado.toUpperCase();
        if (!UFS_VALIDAS.contains(uf)) {
            throw new PessoaException("O estado " + estado + " não é uma UF brasileira válida");
        }
        return uf;
    }
}
