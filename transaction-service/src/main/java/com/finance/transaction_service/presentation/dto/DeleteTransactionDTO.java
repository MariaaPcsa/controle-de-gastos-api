package com.finance.transaction_service.presentation.dto;

import java.util.UUID;

public class DeleteTransactionDTO {

    private UUID transactionId;
    private Boolean confirmDelete;
    private String reason;

    // ================= CONSTRUCTORS =================
    public DeleteTransactionDTO() {
        this.confirmDelete = false;
    }

    public DeleteTransactionDTO(UUID transactionId) {
        this.transactionId = transactionId;
        this.confirmDelete = true;
    }

    public DeleteTransactionDTO(UUID transactionId, Boolean confirmDelete, String reason) {
        this.transactionId = transactionId;
        this.confirmDelete = confirmDelete != null ? confirmDelete : false;
        this.reason = reason;
    }

    // ================= GETTERS & SETTERS =================
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public Boolean getConfirmDelete() { return confirmDelete; }
    public void setConfirmDelete(Boolean confirmDelete) { this.confirmDelete = confirmDelete; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    // ================= VALIDATION =================
    public boolean isValid() {
        return transactionId != null && confirmDelete != null && confirmDelete;
    }

    @Override
    public String toString() {
        return "DeleteTransactionDTO{" +
                "transactionId=" + transactionId +
                ", confirmDelete=" + confirmDelete +
                ", reason='" + reason + '\'' +
                '}';
    }
}
