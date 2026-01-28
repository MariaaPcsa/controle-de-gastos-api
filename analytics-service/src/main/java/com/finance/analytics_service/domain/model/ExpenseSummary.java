package com.finance.analytics_service.domain.model;



import java.math.BigDecimal;

public class ExpenseSummary {

    private String category;
    private BigDecimal total;

    public ExpenseSummary(String category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
