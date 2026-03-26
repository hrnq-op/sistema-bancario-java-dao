package br.com.henrique.estudos.banco.model;

import br.com.henrique.estudos.banco.exceptions.SaldoInsuficienteException;
import br.com.henrique.estudos.banco.exceptions.ValorInvalidoException;

public abstract class Conta {
   private int agencia;
   private int numero;
   private Cliente titular;
   private double saldo;

   public Conta(int agencia, int numero, Cliente titular) throws ValorInvalidoException{
       this.setAgencia(agencia);
       this.setNumero(numero);
       this.setTitular(titular);
   }

   public void depositar(double valor) throws ValorInvalidoException {
       if (valor <= 0){
           throw new ValorInvalidoException("O valor do depósito deve ser positivo. Valor informado: " + valor);
       }else{
           this.setSaldo(this.getSaldo() + valor);
       }
   }

   public abstract void sacar (double valor) throws SaldoInsuficienteException, ValorInvalidoException;

   public void transferir (double valor, Conta destino) throws  SaldoInsuficienteException, ValorInvalidoException {
       if (destino == null){
           throw new ValorInvalidoException("A conta de destino não pode ser nula.");
       }else{
           this.sacar(valor);
           destino.depositar(valor);
       }
   }

   public void subtrairSaldo(double valor) {
       this.setSaldo(this.getSaldo() - valor);
   }

   public int getAgencia() {
       return this.agencia;
   }
   public void setAgencia(int agencia) throws ValorInvalidoException {
       if (agencia <= 0) {
           throw new ValorInvalidoException("O número da agência deve ser maior que zero.");
       } else this.agencia = agencia;
   }
   public int getNumero() {
       return this.numero;
   }
   public void setNumero(int numero) throws ValorInvalidoException {
       if (numero <= 0){
           throw new ValorInvalidoException("O número da conta deve ser maior que zero.");
       } else this.numero = numero;
   }
   public Cliente getTitular() {
       return this.titular;
   }
   public void setTitular(Cliente titular) throws ValorInvalidoException {
       if (titular == null){
           throw new ValorInvalidoException("O Titular não pode ser nulo.");
       }else this.titular = titular;
   }
   public double getSaldo(){
       return this.saldo;
   }
   public void setSaldo(double saldo){
       this.saldo = saldo;
   }

    @Override
    public String toString() {
        return String.format("""
                        --- Conta ---\s
                        Agência: %d\s
                        Número: %d\s
                        Titular: %s\s
                        Saldo: R$ %.2f
                        """,
                this.getAgencia(), this.getNumero(), this.getTitular().getNome(), this.getSaldo());
    }
}