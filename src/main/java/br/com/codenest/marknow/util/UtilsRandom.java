package br.com.codenest.marknow.util;

import java.security.SecureRandom;
import java.util.UUID;

public final class UtilsRandom {

    private UtilsRandom() {
    }

    private static final SecureRandom RNG = new SecureRandom();

    /**
     * UUID padrão.
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Número aleatório somente com dígitos (primeiro dígito não-zero).
     */
    public static String numeroDigitos(int tamanho) {
        if (tamanho <= 0) return "";
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            int digit = (i == 0) ? (RNG.nextInt(9) + 1) : RNG.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    /**
     * String aleatória base16 (não é token criptográfico forte para auth, é util).
     */
    public static String hex(int bytes) {
        if (bytes <= 0) return "";
        byte[] b = new byte[bytes];
        RNG.nextBytes(b);
        StringBuilder sb = new StringBuilder(bytes * 2);
        for (byte v : b) {
            sb.append(Character.forDigit((v >> 4) & 0xF, 16));
            sb.append(Character.forDigit(v & 0xF, 16));
        }
        return sb.toString();
    }
}
