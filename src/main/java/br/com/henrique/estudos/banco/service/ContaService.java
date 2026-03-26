package br.com.henrique.estudos.banco.service;

import br.com.henrique.estudos.banco.dao.IContaDAO;
import br.com.henrique.estudos.banco.exceptions.SaldoInsuficienteException;
import br.com.henrique.estudos.banco.exceptions.ValorInvalidoException;
import br.com.henrique.estudos.banco.model.*;

import java.util.List;

public class ContaService {

    private final IContaDAO contaDAO;

    public ContaService(IContaDAO contaDAO) {
        this.contaDAO = contaDAO;
    }

    public void salvarNovaConta(Conta conta) {
        if (conta != null) {
            contaDAO.salvar(conta);
        }
    }

    public void sacar(int numeroConta, double valor) throws SaldoInsuficienteException, ValorInvalidoException {
        Conta conta = contaDAO.buscarPorNumero(numeroConta);

        if (conta == null){
            throw new ValorInvalidoException("Conta número \" + numeroConta + \" não encontrada.");
        }
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor do saque deve ser positivo.");
        }

        conta.sacar(valor);
        contaDAO.atualizar(conta);

        System.out.println("Saque de R$ " + String.format("%.2f", valor) + " realizado com sucesso!");
    }

    public void depositar (int numeroConta, double valor) throws ValorInvalidoException {
        Conta conta = contaDAO.buscarPorNumero(numeroConta);

        if (conta == null){
            throw new ValorInvalidoException("Conta número \" + numeroConta + \" não encontrada.");
        }
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor do saque deve ser positivo.");
        }

        conta.depositar(valor);
        contaDAO.atualizar(conta);

        System.out.println("Depósito de R$ " + String.format("%.2f", valor) + " realizado com sucesso!");
    }

    public void transferir(int numOrigem, int numDestino, double valor) throws SaldoInsuficienteException, ValorInvalidoException {
        Conta origem = contaDAO.buscarPorNumero(numOrigem);
        Conta destino = contaDAO.buscarPorNumero(numDestino);

        if (origem == null || destino == null) {
            throw new ValorInvalidoException("Conta de origem ou destino não encontrada.");
        }

        origem.transferir(valor, destino);

        contaDAO.atualizar(origem);
        contaDAO.atualizar(destino);

        System.out.println("Transferência de R$ " + valor + " realizada e salva no banco!");
    }

    public void aplicarRendimentoMensal() {
        double taxa = 0.5;

        List<Conta> contas = contaDAO.listarTodos();

        for (Conta conta : contas) {
            if (conta instanceof ContaPoupanca poupanca) {
                try {
                    poupanca.aplicarRendimento(taxa);
                    contaDAO.atualizar(poupanca);

                    System.out.println("Rendimento aplicado na conta " + poupanca.getNumero());

                } catch (Exception e) {
                    System.err.println("Erro ao processar conta " + poupanca.getNumero() + ": " + e.getMessage());
                }
            }
        }
    }
}