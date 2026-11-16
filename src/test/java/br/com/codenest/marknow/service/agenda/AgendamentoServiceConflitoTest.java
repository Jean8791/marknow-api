package br.com.codenest.marknow.service.agenda;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendamentoServiceConflitoTest {

    private static PersistenceException erroDoBanco(String sqlState) {
        return new PersistenceException(new RuntimeException(new SQLException("falha", sqlState)));
    }

    @Test
    void deveTratarComoConflitoQuandoOBancoViolaAExclusao() {
        assertTrue(AgendamentoService.conflitoDeHorario(erroDoBanco("23P01")));
    }

    @Test
    void deveTratarComoConflitoQuandoOBancoDetectaDeadlockNaDisputa() {
        assertTrue(AgendamentoService.conflitoDeHorario(erroDoBanco("40P01")));
    }

    @Test
    void naoDeveTratarComoConflitoOutrosErrosDoBanco() {
        assertFalse(AgendamentoService.conflitoDeHorario(erroDoBanco("23505")));
        assertFalse(AgendamentoService.conflitoDeHorario(new PersistenceException("sem causa SQL")));
    }
}
