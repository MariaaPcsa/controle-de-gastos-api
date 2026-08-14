package com.finance.transaction_service.infrastructure.external.helper;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class CotacaoHelper {

    // Armazena as cotações já consultadas (TreeMap mantém ordem por data)
    private final Map<LocalDate, BigDecimal> historico = new TreeMap<>();

    /**
     * Retorna a cotação para a data desejada.
     * Se não houver cotação, retorna a última cotação conhecida.
     */
    public BigDecimal getCotacao(LocalDate data) {
        if (historico.containsKey(data)) {
            return historico.get(data);
        }

        // Se for fim de semana, pega a última cotação conhecida
        if (data.getDayOfWeek() == DayOfWeek.SATURDAY ||
                data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return historico.isEmpty() ? BigDecimal.ONE : ((TreeMap<LocalDate, BigDecimal>) historico).lastEntry().getValue();
        }

        // Se não tiver histórico, retorna 1 como placeholder
        return historico.isEmpty() ? BigDecimal.ONE : ((TreeMap<LocalDate, BigDecimal>) historico).lastEntry().getValue();
    }

    /**
     * Registra uma cotação para uma data específica
     */
    public void registrarCotacao(LocalDate data, BigDecimal valor) {
        historico.put(data, valor);
    }
}
