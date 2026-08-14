package com.finance.transaction_service.presentation.dto;

import java.util.List;

public class PagedResponseDTO<T> {

    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean isFirst;
    private Boolean isLast;
    private Boolean hasNext;
    private Boolean hasPrevious;

    // ================= CONSTRUCTORS =================
    public PagedResponseDTO() {}

    public PagedResponseDTO(List<T> content, Integer pageNumber, Integer pageSize,
                           Long totalElements, Integer totalPages) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isFirst = pageNumber == 0;
        this.isLast = pageNumber >= (totalPages - 1);
        this.hasNext = pageNumber < (totalPages - 1);
        this.hasPrevious = pageNumber > 0;
    }

    // ================= FACTORY METHOD =================
    public static <T> PagedResponseDTO<T> of(List<T> content, Integer pageNumber,
                                              Integer pageSize, Long totalElements) {
        Integer totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new PagedResponseDTO<>(content, pageNumber, pageSize, totalElements, totalPages);
    }

    // ================= GETTERS & SETTERS =================
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }

    public Boolean getIsFirst() { return isFirst; }
    public void setIsFirst(Boolean isFirst) { this.isFirst = isFirst; }

    public Boolean getIsLast() { return isLast; }
    public void setIsLast(Boolean isLast) { this.isLast = isLast; }

    public Boolean getHasNext() { return hasNext; }
    public void setHasNext(Boolean hasNext) { this.hasNext = hasNext; }

    public Boolean getHasPrevious() { return hasPrevious; }
    public void setHasPrevious(Boolean hasPrevious) { this.hasPrevious = hasPrevious; }

    @Override
    public String toString() {
        return "PagedResponseDTO{" +
                "content=" + content +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", isFirst=" + isFirst +
                ", isLast=" + isLast +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}

