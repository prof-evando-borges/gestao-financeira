package br.com.fiap.gestaofinanceira.contas.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.contas.entity.ChavePix;
import br.com.fiap.gestaofinanceira.contas.entity.ContaBancaria;
import br.com.fiap.gestaofinanceira.contas.entity.StatusConta;
import br.com.fiap.gestaofinanceira.contas.entity.TipoChavePix;
import br.com.fiap.gestaofinanceira.contas.exception.ContaException;
import br.com.fiap.gestaofinanceira.contas.repository.ChavePixRepository;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ChavePixService {

    // Limite do Banco Central para contas de pessoa fisica
    private static final int MAXIMO_DE_CHAVES_POR_CONTA = 5;

    private final ChavePixRepository chavePixRepository;
    private final ContaBancariaService contaBancariaService;
    // Modulo 01 (Pessoas): usado para conferir que a chave CPF e do titular
    private final UsuarioService usuarioService;

    public ChavePixService(ChavePixRepository chavePixRepository,
                           ContaBancariaService contaBancariaService,
                           UsuarioService usuarioService) {
        this.chavePixRepository = chavePixRepository;
        this.contaBancariaService = contaBancariaService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public ChavePix cadastrar(ChavePix chave) {
        ContaBancaria conta = contaBancariaService.buscarPorId(idDaConta(chave)); // 404 se a conta nao existir
        // Regra: so conta ATIVA recebe chave nova
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaException("Só é possível cadastrar chave Pix em conta ATIVA; a conta está " + conta.getStatus());
        }
        // Regra: no maximo 5 chaves por conta
        if (chavePixRepository.countByContaId(conta.getId()) >= MAXIMO_DE_CHAVES_POR_CONTA) {
            throw new ContaException("A conta já tem o máximo de " + MAXIMO_DE_CHAVES_POR_CONTA + " chaves Pix");
        }
        chave.setValor(normalizar(chave.getTipo(), chave.getValor()));
        validarValor(chave.getTipo(), chave.getValor(), conta);
        // Regra: a mesma chave nao pode existir duas vezes no sistema
        if (chavePixRepository.existsByValor(chave.getValor())) {
            throw new ContaException("A chave Pix " + chave.getValor() + " já está cadastrada");
        }

        chave.setId(null);
        chave.setConta(conta);
        chave.setCriadoEm(null);
        return chavePixRepository.save(chave);
    }

    public List<ChavePix> listarTodos() {
        return chavePixRepository.findAll();
    }

    public List<ChavePix> listar(String idConta) {
        if (idConta == null) {
            return listarTodos();
        }
        return chavePixRepository.findByContaId(idConta);
    }

    public ChavePix buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id da chave Pix");
        }
        return chavePixRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chave Pix não encontrada: " + id));
    }

    @Transactional
    public ChavePix atualizar(String id, ChavePix dados) {
        ChavePix chave = buscarPorId(id);
        ContaBancaria conta = contaBancariaService.buscarPorId(idDaConta(dados));
        boolean trocouDeConta = !conta.getId().equals(chave.getConta().getId());

        if (Boolean.TRUE.equals(dados.getAtiva()) && conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaException("A chave só pode ficar ativa em conta ATIVA; a conta está " + conta.getStatus());
        }
        if (trocouDeConta && chavePixRepository.countByContaId(conta.getId()) >= MAXIMO_DE_CHAVES_POR_CONTA) {
            throw new ContaException("A conta já tem o máximo de " + MAXIMO_DE_CHAVES_POR_CONTA + " chaves Pix");
        }
        String valor = normalizar(dados.getTipo(), dados.getValor());
        validarValor(dados.getTipo(), valor, conta);
        if (chavePixRepository.existsByValorAndIdNot(valor, id)) {
            throw new ContaException("A chave Pix " + valor + " já está cadastrada");
        }

        chave.setConta(conta);
        chave.setTipo(dados.getTipo());
        chave.setValor(valor);
        chave.setAtiva(dados.getAtiva());
        return chavePixRepository.save(chave);
    }

    @Transactional
    public void remover(String id) {
        chavePixRepository.delete(buscarPorId(id));
    }

    private String normalizar(TipoChavePix tipo, String valor) {
        String limpo = valor.trim();
        return switch (tipo) {
            case CPF -> limpo.replaceAll("[.\\-\\s]", "");
            case TELEFONE -> limpo.replaceAll("[()\\-\\s]", "");
            case EMAIL, ALEATORIA -> limpo.toLowerCase(Locale.ROOT);
        };
    }

    private void validarValor(TipoChavePix tipo, String valor, ContaBancaria conta) {
        switch (tipo) {
            case CPF -> {
                if (!valor.matches("\\d{11}")) {
                    throw new ContaException("A chave CPF deve ter 11 dígitos");
                }
                // Regra: a chave CPF tem que ser o CPF do titular da conta
                String cpfDoTitular = usuarioService.buscarPorId(conta.getIdUsuario()).getCpf();
                if (!valor.equals(cpfDoTitular)) {
                    throw new ContaException("A chave CPF deve ser o CPF do titular da conta");
                }
            }
            case EMAIL -> {
                if (!valor.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
                    throw new ContaException("A chave EMAIL deve ser um e-mail válido, ex.: nome@dominio.com");
                }
            }
            case TELEFONE -> {
                if (!valor.matches("\\+55\\d{10,11}")) {
                    throw new ContaException("A chave TELEFONE deve estar no formato +55DDDNUMERO, ex.: +5511987654321");
                }
            }
            case ALEATORIA -> {
                if (!valor.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                    throw new ContaException("A chave ALEATORIA deve ser um UUID, ex.: 123e4567-e89b-12d3-a456-426614174000");
                }
            }
        }
    }

    private String idDaConta(ChavePix chave) {
        if (chave.getConta() == null || chave.getConta().getId() == null) {
            throw new ContaException("Informe a conta da chave: \"conta\": { \"id\": \"...\" }");
        }
        return chave.getConta().getId();
    }
}
