package br.com.henrique.estudos.banco.exceptions;

public class CpfInvalidoException extends Exception {
    public CpfInvalidoException(String message) {
        super(message);
    }
}
