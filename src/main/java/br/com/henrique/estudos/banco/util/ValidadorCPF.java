package br.com.henrique.estudos.banco.util;

import java.util.Objects;

public class ValidadorCPF {

    private static final int CPF_LENGTH = 11;
    private static final int[] CPF_WEIGHTS = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    private ValidadorCPF() {
        throw new UnsupportedOperationException("Classe Utilitária");
    }

    public static boolean ehValido(final String cpf) {
        int[] digits = extractToArray(cpf);

        // Verifica se tem 11 dígitos e se não são todos iguais (ex: 111.111.111-11)
        if (digits.length != CPF_LENGTH || isAllSame(digits)) {
            return false;
        }

        // Validação do primeiro e segundo dígito verificador
        return calcularDigito(digits, CPF_WEIGHTS, 9) == digits[9]
                && calcularDigito(digits, CPF_WEIGHTS, 10) == digits[10];
    }

    public static String formatar(final String cpf) {
        String limpo = cpf.replaceAll("\\D", "");
        if (limpo.length() == CPF_LENGTH) {
            return limpo.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return limpo;
    }

    private static int calcularDigito(final int[] digits, final int[] weights, final int length) {
        int sum = 0;
        final int weightOffset = weights.length - length;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * weights[weightOffset + i];
        }
        int remainder = sum % 11;
        int result = 11 - remainder;
        return result >= 10 ? 0 : result;
    }

    private static int[] extractToArray(final String s) {
        if (Objects.isNull(s) || s.isBlank()) {
            return new int[0];
        }
        // Remove tudo que não for número
        String onlyNumbers = s.replaceAll("\\D", "");
        int[] digits = new int[onlyNumbers.length()];
        for (int i = 0; i < onlyNumbers.length(); i++) {
            digits[i] = Character.getNumericValue(onlyNumbers.charAt(i));
        }
        return digits;
    }

    private static boolean isAllSame(final int[] digits) {
        if (digits.length == 0) return true;
        int first = digits[0];
        for (int i = 1; i < digits.length; i++) {
            if (digits[i] != first) return false;
        }
        return true;
    }
}