package br.com.henrique.estudos.banco.model;

import br.com.henrique.estudos.banco.exceptions.SaldoInsuficienteException;
import br.com.henrique.estudos.banco.exceptions.ValorInvalidoException;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(int agencia, int numero, Cliente titular) throws ValorInvalidoException {
        super(agencia, numero, titular);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (this.getSaldo() < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente na poupança.");
        }
        this.subtrairSaldo(valor);
    }

    public void aplicarRendimento(double percentual) throws ValorInvalidoException {
        double rendimento = this.getSaldo() * (percentual / 100);
        this.depositar(rendimento);
    }
}