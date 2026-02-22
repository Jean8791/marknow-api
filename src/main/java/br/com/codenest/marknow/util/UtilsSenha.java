package br.com.codenest.marknow.util;

import io.quarkus.elytron.security.common.BcryptUtil;

public final class UtilsSenha {

    private UtilsSenha() {
    }

    /**
     * Transforma a senha "123" em um hash seguro.
     */
    public static String criptografar(String senhaPura) {
        if (senhaPura == null || senhaPura.isBlank()) return null;
        return BcryptUtil.bcryptHash(senhaPura);
    }

    /**
     * Compara a senha digitada no login com o hash que está no banco.
     */
    public static boolean verificar(String senhaPura, String hashBanco) {
        if (senhaPura == null || hashBanco == null) return false;
        try {
            return BcryptUtil.matches(senhaPura, hashBanco);
        } catch (Exception e) {
            return false;
        }
    }
}