package br.com.henrique.estudos.banco.model;

import br.com.henrique.estudos.banco.exceptions.SaldoInsuficienteException;
import br.com.henrique.estudos.banco.exceptions.ValorInvalidoException;

public class ContaCorrente extends Conta {

    private double limite;

    public ContaCorrente(int agencia, int numero, Cliente titular, double limite) throws ValorInvalidoException {
        super(agencia, numero, titular);
        this.setLimite(limite);
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException, ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor do saque deve ser positivo.");
        }

        if (valor > (getSaldo() + this.limite)) {
            throw new SaldoInsuficienteException("Saldo e limite insuficientes! " +
                    "Disponível total: R$ " + (getSaldo() + this.limite));
        }

        this.subtrairSaldo(valor);
    }

    public double getLimite() { return this.limite; }

    public void setLimite(double limite) {
        if (limite >= 0) this.limite = limite;
    }
}