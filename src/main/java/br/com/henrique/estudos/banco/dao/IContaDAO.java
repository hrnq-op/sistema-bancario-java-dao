package br.com.henrique.estudos.banco.dao;

import br.com.henrique.estudos.banco.model.Conta;
import java.util.List;

public interface IContaDAO {
    void salvar(Conta conta);
    void atualizar(Conta conta);
    void deletar(int numero);
    Conta buscarPorNumero(int numero);
    List<Conta> listarTodos();
    Conta buscarPorLogin(String login, String senha);
}