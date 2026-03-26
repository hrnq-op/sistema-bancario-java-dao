package br.com.henrique.estudos.banco.exceptions;

public class ValorInvalidoException extends Exception{
    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}