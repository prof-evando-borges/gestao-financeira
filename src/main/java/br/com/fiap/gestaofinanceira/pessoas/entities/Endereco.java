package br.com.fiap.gestaofinanceira.pessoas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "TB_PES_ENDERECO")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36, nullable = false)
    private String id;

    // EAGER: o usuario aparece no JSON e o open-in-view esta desligado.
    // No cadastro basta mandar "usuario": { "id": "..." }
    @NotNull(message = "O usuário do endereço é obrigatório")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "O CEP deve ter 8 dígitos, somente números")
    @Column(name = "CEP", length = 8, nullable = false)
    private String cep;

    @NotBlank(message = "O logradouro é obrigatório")
    @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres")
    @Column(name = "LOGRADOURO", length = 150, nullable = false)
    private String logradouro;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "O número deve ter no máximo 10 caracteres")
    @Column(name = "NUMERO", length = 10, nullable = false)
    private String numero;

    @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    @Column(name = "COMPLEMENTO", length = 100)
    private String complemento;

    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    @Column(name = "CIDADE", length = 100, nullable = false)
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve ser a sigla da UF com 2 letras (ex.: SP)")
    @Column(name = "ESTADO", length = 2, nullable = false)
    private String estado;

    public Endereco() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
