package br.com.codenest.marknow.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UtilsDataTest {

    @Test
    void parseIsoParaInstant_nullOuVazio() {
        assertNull(UtilsData.parseIsoParaInstant(null));
        assertNull(UtilsData.parseIsoParaInstant("   "));
    }

    @Test
    void parseIsoParaInstant_aceitaZ() {
        Instant i = UtilsData.parseIsoParaInstant("2026-02-13T12:00:00Z");
        assertEquals(Instant.parse("2026-02-13T12:00:00Z"), i);
    }

    @Test
    void parseIsoParaInstant_aceitaOffset_eEquivaleAoMesmoInstante() {
        // 12:00Z == 09:00-03:00 (mesmo instante)
        Instant z = UtilsData.parseIsoParaInstant("2026-02-13T12:00:00Z");
        Instant off = UtilsData.parseIsoParaInstant("2026-02-13T09:00:00-03:00");
        assertEquals(z, off);
    }

    @Test
    void parseIsoParaLocalDate_nullOuVazio() {
        assertNull(UtilsData.parseIsoParaLocalDate(null));
        assertNull(UtilsData.parseIsoParaLocalDate(" "));
    }

    @Test
    void parseIsoParaLocalDate_ok() {
        assertEquals(LocalDate.of(2026, 2, 13), UtilsData.parseIsoParaLocalDate("2026-02-13"));
        assertEquals(LocalDate.of(2026, 2, 13), UtilsData.parseIsoParaLocalDate(" 2026-02-13 "));
    }

    @Test
    void inicioDoDia_retornaMeiaNoiteNaZonaPadrao() {
        LocalDate d = LocalDate.of(2026, 2, 13);
        Instant i = UtilsData.inicioDoDia(d);

        ZonedDateTime zdt = i.atZone(UtilsData.ZONA_PADRAO);
        assertEquals(2026, zdt.getYear());
        assertEquals(2, zdt.getMonthValue());
        assertEquals(13, zdt.getDayOfMonth());
        assertEquals(0, zdt.getHour());
        assertEquals(0, zdt.getMinute());
        assertEquals(0, zdt.getSecond());
    }

    @Test
    void zerarHora_truncaParaInicioDoDiaNaZonaPadrao() {
        // 2026-02-13 15:45 na zona padrão
        ZonedDateTime zdt = ZonedDateTime.of(
                2026, 2, 13,
                15, 45, 30, 123_000_000,
                UtilsData.ZONA_PADRAO
        );
        Instant original = zdt.toInstant();

        Instant zerado = UtilsData.zerarHora(original);
        ZonedDateTime z2 = zerado.atZone(UtilsData.ZONA_PADRAO);

        assertEquals(2026, z2.getYear());
        assertEquals(2, z2.getMonthValue());
        assertEquals(13, z2.getDayOfMonth());
        assertEquals(0, z2.getHour());
        assertEquals(0, z2.getMinute());
        assertEquals(0, z2.getSecond());
        assertEquals(0, z2.getNano());
    }

    @Test
    void diferencaDias_calculaPeloLocalDateNaZonaPadrao() {
        // 2026-02-13 10:00 (SP) -> 2026-02-15 09:00 (SP) = 2 dias de diferença por LocalDate
        Instant ini = ZonedDateTime.of(2026, 2, 13, 10, 0, 0, 0, UtilsData.ZONA_PADRAO).toInstant();
        Instant fim = ZonedDateTime.of(2026, 2, 15, 9, 0, 0, 0, UtilsData.ZONA_PADRAO).toInstant();

        assertEquals(2, UtilsData.diferencaDias(ini, fim));
        assertEquals(-2, UtilsData.diferencaDias(fim, ini));
    }

    @Test
    void diferencaDias_casoPertoDaMeiaNoite_importaZonaPadrao() {
        // Aqui a intenção é garantir que você está usando LocalDate na zona de SP, não UTC.
        // 2026-02-13 00:30 SP
        Instant ini = ZonedDateTime.of(2026, 2, 13, 0, 30, 0, 0, UtilsData.ZONA_PADRAO).toInstant();
        // 2026-02-14 00:10 SP
        Instant fim = ZonedDateTime.of(2026, 2, 14, 0, 10, 0, 0, UtilsData.ZONA_PADRAO).toInstant();

        assertEquals(1, UtilsData.diferencaDias(ini, fim));
    }

    @Test
    void somarMinutos_somaEDiminui() {
        Instant base = Instant.parse("2026-02-13T12:00:00Z");
        assertEquals(Instant.parse("2026-02-13T12:30:00Z"), UtilsData.somarMinutos(base, 30));
        assertEquals(Instant.parse("2026-02-13T11:30:00Z"), UtilsData.somarMinutos(base, -30));
        assertNull(UtilsData.somarMinutos(null, 10));
    }

    @Test
    void ponteDateInstant_idaEVolta() {
        Instant base = Instant.parse("2026-02-13T12:00:00Z");

        Date d = UtilsData.toDate(base);
        assertNotNull(d);

        Instant i2 = UtilsData.toInstant(d);
        assertEquals(base, i2);

        assertNull(UtilsData.toDate(null));
        assertNull(UtilsData.toInstant((Date) null));
    }

    @Test
    void zonaPadrao_constanteCorreta() {
        assertEquals(ZoneId.of("America/Sao_Paulo"), UtilsData.ZONA_PADRAO);
    }
}
