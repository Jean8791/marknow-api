package br.com.codenest.marknow.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmailService {

    @Transactional
    public void enviarEmailConvite() {

    }
}
