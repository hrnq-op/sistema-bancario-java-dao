package br.com.henrique.estudos.banco.model;

import br.com.henrique.estudos.banco.exceptions.CpfInvalidoException;
import br.com.henrique.estudos.banco.exceptions.ValorInvalidoException;
import br.com.henrique.estudos.banco.util.ValidadorCPF;

public class Cliente {
    private String nome;
    private String cpf;
    private String profissao;
    private String login;
    private String senha;

    public Cliente(String nome, String cpf, String profissao, String login, String senha) throws CpfInvalidoException, ValorInvalidoException {
        this.setNome(nome);
        this.setCpf(cpf);
        this.setProfissao(profissao);
        this.setLogin(login);
        this.setSenha(senha);
    }

    public String getNome() {
        return this.nome;
    }
    public void setNome(String nome) throws ValorInvalidoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValorInvalidoException("O nome não pode estar vazio.");
        }
        this.nome = nome;
    }

    public String getCpf() {
        return this.cpf;
    }
    public void setCpf(String cpf) throws CpfInvalidoException{
            if (ValidadorCPF.ehValido(cpf)) {
                this.cpf = ValidadorCPF.formatar(cpf);
            } else {
                throw new CpfInvalidoException("CPF inválido!");
            }
    }

    public String getProfissao() {
        return this.profissao;
    }
    public void setProfissao(String profissao) throws ValorInvalidoException {
        if (profissao == null || profissao.trim().isEmpty()) {
            throw new ValorInvalidoException("Se não tiver emprego, escreva 'Desempregado'.");
        }
        this.profissao = profissao;
    }
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) throws ValorInvalidoException {
        if (login == null || login.length() < 4) {
            throw new ValorInvalidoException("O login deve ter no minimo 4 caracteres.");
        }
        this.login = login;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) throws ValorInvalidoException {
        if (senha == null || senha.length() < 4) {
            throw new ValorInvalidoException("A senha deve ser mais forte (minimo 4 caracteres).");
        }
        this.senha = senha;
    }
}