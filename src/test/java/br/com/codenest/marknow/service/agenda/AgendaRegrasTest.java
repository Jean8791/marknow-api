package br.com.codenest.marknow.service.agenda;

import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.service.agenda.AgendaRegras.Intervalo;
import br.com.codenest.marknow.service.agenda.AgendaRegras.Janela;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgendaRegrasTest {

    private static final LocalDate DATA = LocalDate.of(2030, 1, 7);
    private static final LocalDateTime ANTES = LocalDateTime.of(2030, 1, 1, 0, 0);
    private static final List<Janela> MANHA_E_TARDE = List.of(
            new Janela(LocalTime.of(8, 0), LocalTime.of(10, 0)),
            new Janela(LocalTime.of(14, 0), LocalTime.of(15, 0)));

    private static LocalTime h(int hora, int minuto) {
        return LocalTime.of(hora, minuto);
    }

    @Test
    void alinhadoAoSlot_aceitaSomenteHoraCheiaOuMeiaHora() {
        assertTrue(AgendaRegras.alinhadoAoSlot(h(9, 0)));
        assertTrue(AgendaRegras.alinhadoAoSlot(h(9, 30)));
        assertFalse(AgendaRegras.alinhadoAoSlot(h(9, 15)));
        assertFalse(AgendaRegras.alinhadoAoSlot(LocalTime.of(9, 0, 10)));
    }

    @Test
    void validarDuracao_exigeMultiploDe30AteOitoHoras() {
        assertDoesNotThrow(() -> AgendaRegras.validarDuracao(30));
        assertDoesNotThrow(() -> AgendaRegras.validarDuracao(480));
        assertThrows(BusinessException.class, () -> AgendaRegras.validarDuracao(45));
        assertThrows(BusinessException.class, () -> AgendaRegras.validarDuracao(0));
        assertThrows(BusinessException.class, () -> AgendaRegras.validarDuracao(510));
        assertThrows(BusinessException.class, () -> AgendaRegras.validarDuracao(null));
    }

    @Test
    void validarJanelas_recusaFimAntesDoInicioMinutoQuebradoESobreposicao() {
        assertDoesNotThrow(() -> AgendaRegras.validarJanelas(List.of(1, 1), MANHA_E_TARDE));
        assertThrows(BusinessException.class, () ->
                AgendaRegras.validarJanelas(List.of(1), List.of(new Janela(h(12, 0), h(8, 0)))));
        assertThrows(BusinessException.class, () ->
                AgendaRegras.validarJanelas(List.of(1), List.of(new Janela(h(8, 15), h(12, 0)))));
        assertThrows(BusinessException.class, () ->
                AgendaRegras.validarJanelas(List.of(1, 1), List.of(new Janela(h(8, 0), h(12, 0)), new Janela(h(11, 0), h(13, 0)))));
        assertThrows(BusinessException.class, () ->
                AgendaRegras.validarJanelas(List.of(8), List.of(new Janela(h(8, 0), h(12, 0)))));
    }

    @Test
    void validarJanelas_permiteMesmoHorarioEmDiasDiferentes() {
        assertDoesNotThrow(() -> AgendaRegras.validarJanelas(List.of(1, 2),
                List.of(new Janela(h(8, 0), h(12, 0)), new Janela(h(8, 0), h(12, 0)))));
    }

    @Test
    void cabeEmAlgumaJanela_consideraADuracaoInteira() {
        assertTrue(AgendaRegras.cabeEmAlgumaJanela(h(9, 0), 60, MANHA_E_TARDE));
        assertFalse(AgendaRegras.cabeEmAlgumaJanela(h(9, 30), 60, MANHA_E_TARDE));
        assertFalse(AgendaRegras.cabeEmAlgumaJanela(h(12, 0), 30, MANHA_E_TARDE));
        assertFalse(AgendaRegras.cabeEmAlgumaJanela(h(23, 30), 60, List.of(new Janela(h(23, 0), h(23, 30)))));
    }

    @Test
    void horariosLivres_geraSlotsDe30MinQueCabemNaJanela() {
        List<LocalTime> livres = AgendaRegras.horariosLivres(DATA, MANHA_E_TARDE, 60, List.of(), ANTES);
        assertEquals(List.of(h(8, 0), h(8, 30), h(9, 0), h(14, 0)), livres);
    }

    @Test
    void horariosLivres_removeOsQueSobrepoemAgendamentos() {
        List<Intervalo> ocupados = List.of(new Intervalo(DATA.atTime(8, 30), DATA.atTime(9, 0)));
        List<LocalTime> livres = AgendaRegras.horariosLivres(DATA, MANHA_E_TARDE, 60, ocupados, ANTES);
        assertEquals(List.of(h(9, 0), h(14, 0)), livres);
    }

    @Test
    void horariosLivres_ignoraHorariosNoPassado() {
        List<LocalTime> livres = AgendaRegras.horariosLivres(DATA, MANHA_E_TARDE, 30, List.of(), DATA.atTime(9, 0));
        assertEquals(List.of(h(9, 30), h(14, 0), h(14, 30)), livres);
    }

    @Test
    void horariosLivres_semJanelaNoDiaRetornaVazio() {
        assertTrue(AgendaRegras.horariosLivres(DATA, List.of(), 30, List.of(), ANTES).isEmpty());
    }
}
