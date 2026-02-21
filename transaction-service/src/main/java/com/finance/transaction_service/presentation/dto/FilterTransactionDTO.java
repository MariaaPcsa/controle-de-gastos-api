package com.finance.transaction_service.presentation.dto;

import com.finance.transaction_service.domain.model.TransactionType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class FilterTransactionDTO {

    private String category;
    private TransactionType type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;

    // ================= CONSTRUCTORS =================
    public FilterTransactionDTO() {
        this.page = 0;
        this.pageSize = 10;
        this.sortBy = "createdAt";
        this.sortDirection = "DESC";
    }

    public FilterTransactionDTO(String category, TransactionType type,
                                LocalDateTime startDate, LocalDateTime endDate,
                                Integer page, Integer pageSize,
                                String sortBy, String sortDirection) {
        this.category = category;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = page != null ? page : 0;
        this.pageSize = pageSize != null ? pageSize : 10;
        this.sortBy = sortBy != null ? sortBy : "createdAt";
        this.sortDirection = sortDirection != null ? sortDirection : "DESC";
    }

    // ================= GETTERS & SETTERS =================
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page != null ? page : 0; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize != null && pageSize > 0 ? pageSize : 10; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy != null ? sortBy : "createdAt"; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    // ================= HELPER METHODS =================
    public boolean hasCategory() {
        return category != null && !category.isBlank();
    }

    public boolean hasType() {
        return type != null;
    }

    public boolean hasDateRange() {
        return startDate != null || endDate != null;
    }

    public boolean hasFilters() {
        return hasCategory() || hasType() || hasDateRange();
    }

    @Override
    public String toString() {
        return "FilterTransactionDTO{" +
                "category='" + category + '\'' +
                ", type=" + type +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}

