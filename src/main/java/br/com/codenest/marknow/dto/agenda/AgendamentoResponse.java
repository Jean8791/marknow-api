package br.com.codenest.marknow.dto.agenda;

import br.com.codenest.marknow.enums.AgendamentoStatus;
import br.com.codenest.marknow.enums.WhatsappStatus;

import java.time.LocalDateTime;

public record AgendamentoResponse(
        String oid,
        String oidProfissional,
        String profissionalNome,
        String oidServico,
        String servicoNome,
        String clienteNome,
        String clienteDdd,
        String clienteTelefone,
        String observacao,
        LocalDateTime inicio,
        LocalDateTime fim,
        AgendamentoStatus status,
        WhatsappStatus whatsappStatus
) {
}
