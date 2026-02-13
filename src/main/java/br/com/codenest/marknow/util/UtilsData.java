package br.com.codenest.marknow.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class UtilsData {

    private UtilsData() {
    }

    /**
     * Timezone padrão do produto.
     */
    public static final ZoneId ZONA_PADRAO = ZoneId.of("America/Sao_Paulo");

    /**
     * Parse ISO para Instant. Aceita "Z" e offsets (-03:00).
     */
    public static Instant parseIsoParaInstant(String iso) {
        if (!UtilsString.temTexto(iso)) return null;
        String s = iso.trim();
        try {
            return Instant.parse(s);
        } catch (DateTimeParseException e) {
            return OffsetDateTime.parse(s).toInstant();
        }
    }

    /**
     * Parse ISO para LocalDate (ex.: 2026-02-13).
     */
    public static LocalDate parseIsoParaLocalDate(String isoDate) {
        if (!UtilsString.temTexto(isoDate)) return null;
        return LocalDate.parse(isoDate.trim());
    }

    /**
     * Início do dia na zona padrão -> Instant.
     */
    public static Instant inicioDoDia(LocalDate data) {
        if (data == null) return null;
        return data.atStartOfDay(ZONA_PADRAO).toInstant();
    }

    /**
     * Zera hora/min/seg/ms da data na zona padrão e retorna Instant.
     */
    public static Instant zerarHora(Instant instant) {
        if (instant == null) return null;
        LocalDate d = instant.atZone(ZONA_PADRAO).toLocalDate();
        return d.atStartOfDay(ZONA_PADRAO).toInstant();
    }

    /**
     * Diferença em dias (dataFinal - dataInicial) usando LocalDate na zona padrão.
     */
    public static long diferencaDias(Instant inicial, Instant finall) {
        if (inicial == null || finall == null) return 0;
        LocalDate di = inicial.atZone(ZONA_PADRAO).toLocalDate();
        LocalDate df = finall.atZone(ZONA_PADRAO).toLocalDate();
        return ChronoUnit.DAYS.between(di, df);
    }

    /**
     * Adiciona minutos a um Instant.
     */
    public static Instant somarMinutos(Instant instant, int minutos) {
        if (instant == null) return null;
        return instant.plus(minutos, ChronoUnit.MINUTES);
    }

    // ===== Ponte para código legado que ainda usa java.util.Date =====

    public static Date toDate(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    public static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }
}
