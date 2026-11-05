package br.com.codenest.marknow.service.agenda;

import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AgendaRegras {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    public static final int SLOT_MINUTOS = 30;

    private AgendaRegras() {
    }

    public record Janela(LocalTime inicio, LocalTime fim) {
    }

    public record Intervalo(LocalDateTime inicio, LocalDateTime fim) {
        boolean sobrepoe(LocalDateTime outroInicio, LocalDateTime outroFim) {
            return inicio.isBefore(outroFim) && outroInicio.isBefore(fim);
        }
    }

    public static boolean alinhadoAoSlot(LocalTime hora) {
        return hora.getSecond() == 0 && hora.getNano() == 0 && hora.getMinute() % SLOT_MINUTOS == 0;
    }

    public static void validarDuracao(Integer duracaoMinutos) {
        if (duracaoMinutos == null || duracaoMinutos <= 0 || duracaoMinutos % SLOT_MINUTOS != 0 || duracaoMinutos > 480) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "A duração deve ser múltipla de 30 minutos, até 8 horas.");
        }
    }

    public static void validarJanelas(List<Integer> dias, List<Janela> janelas) {
        for (int i = 0; i < janelas.size(); i++) {
            Integer dia = dias.get(i);
            Janela j = janelas.get(i);
            if (dia == null || dia < 1 || dia > 7) {
                throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Dia da semana inválido (1 = segunda ... 7 = domingo).");
            }
            if (j.inicio() == null || j.fim() == null || !j.fim().isAfter(j.inicio())) {
                throw BusinessException.of(ErrorCode.UNPROCESSABLE, "O fim da janela deve ser depois do início.");
            }
            if (!alinhadoAoSlot(j.inicio()) || !alinhadoAoSlot(j.fim())) {
                throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Os horários das janelas devem terminar em :00 ou :30.");
            }
            for (int k = 0; k < i; k++) {
                Janela outra = janelas.get(k);
                if (dia.equals(dias.get(k)) && j.inicio().isBefore(outra.fim()) && outra.inicio().isBefore(j.fim())) {
                    throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Há janelas sobrepostas no mesmo dia.");
                }
            }
        }
    }

    public static boolean cabeEmAlgumaJanela(LocalTime inicio, int duracaoMinutos, List<Janela> janelasDoDia) {
        LocalTime fim = inicio.plusMinutes(duracaoMinutos);
        if (!fim.isAfter(inicio)) {
            return false;
        }
        return janelasDoDia.stream().anyMatch(j -> !inicio.isBefore(j.inicio()) && !fim.isAfter(j.fim()));
    }

    public static List<LocalTime> horariosLivres(LocalDate data,
                                                 List<Janela> janelasDoDia,
                                                 int duracaoMinutos,
                                                 List<Intervalo> ocupados,
                                                 LocalDateTime agora) {
        List<LocalTime> livres = new ArrayList<>();
        List<Janela> ordenadas = janelasDoDia.stream().sorted(Comparator.comparing(Janela::inicio)).toList();
        for (Janela janela : ordenadas) {
            LocalTime hora = janela.inicio();
            while (hora.plusMinutes(duracaoMinutos).isAfter(hora) && !hora.plusMinutes(duracaoMinutos).isAfter(janela.fim())) {
                LocalDateTime inicio = data.atTime(hora);
                LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);
                boolean livre = inicio.isAfter(agora) && ocupados.stream().noneMatch(o -> o.sobrepoe(inicio, fim));
                if (livre && !livres.contains(hora)) {
                    livres.add(hora);
                }
                LocalTime proxima = hora.plusMinutes(SLOT_MINUTOS);
                if (!proxima.isAfter(hora)) break;
                hora = proxima;
            }
        }
        return livres;
    }
}
