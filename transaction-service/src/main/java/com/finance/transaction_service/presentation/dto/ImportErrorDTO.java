package com.finance.transaction_service.presentation.dto;

public class ImportErrorDTO {
    private int row;
    private String message;

    public ImportErrorDTO() {}

    public ImportErrorDTO(int row, String message) {
        this.row = row;
        this.message = message;
    }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

