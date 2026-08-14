package com.finance.transaction_service.presentation.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResultDTO {
    private int totalRows;
    private int processed;
    private int success;
    private int failed;
    private List<ImportErrorDTO> errors = new ArrayList<>();

    public ImportResultDTO() {}

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getProcessed() { return processed; }
    public void setProcessed(int processed) { this.processed = processed; }

    public int getSuccess() { return success; }
    public void setSuccess(int success) { this.success = success; }

    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }

    public List<ImportErrorDTO> getErrors() { return errors; }
    public void setErrors(List<ImportErrorDTO> errors) { this.errors = errors; }

    public void addError(ImportErrorDTO e) { this.errors.add(e); }
}

