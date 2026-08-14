package com.finance.analytics_service.domain.model;



import java.math.BigDecimal;
import java.util.Map;

public class ExpenseSummary {

    private BigDecimal totalMes;
    private BigDecimal totalAno;
    private Map<String, BigDecimal> totalPorCategoria;

    public ExpenseSummary(BigDecimal totalMes, BigDecimal totalAno, Map<String, BigDecimal> totalPorCategoria) {
        this.totalMes = totalMes;
        this.totalAno = totalAno;
        this.totalPorCategoria = totalPorCategoria;
    }

    public BigDecimal getTotalMes() {
        return totalMes;
    }

    public BigDecimal getTotalAno() {
        return totalAno;
    }

    public Map<String, BigDecimal> getTotalPorCategoria() {
        return totalPorCategoria;
    }
}
