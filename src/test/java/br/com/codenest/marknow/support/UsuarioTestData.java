package br.com.codenest.marknow.support;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;

import java.util.concurrent.atomic.AtomicLong;

public final class UsuarioTestData {

    private UsuarioTestData() {
    }

    private static final AtomicLong SEQ = new AtomicLong(1);

    public static Builder novo() {
        long n = SEQ.getAndIncrement();
        return new Builder().email("user" + n + "@test.local")
                .nome("Usuario " + n)
                .senha("Senha@12345");

    }

    public static final class Builder {
        private String email;
        private String nome;
        private String senha;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder senha(String senha) {
            this.senha = senha;
            return this;
        }

        public UsuarioCreateResquest build() {
            UsuarioCreateResquest req = new UsuarioCreateResquest();
            req.setEmail(email);
            req.setNome(nome);
            req.setSenha(senha);
            return req;
        }
    }
}
