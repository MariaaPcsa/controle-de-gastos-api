package com.maria.finance.user.infrastructure.excel;

import java.util.ArrayList;
import java.util.List;

public class UserImportResult {

    private int total;
    private int success;
    private int duplicatedEmails;
    private int invalidRows;
    private List<String> errors = new ArrayList<>();

    public void incrementTotal() { total++; }
    public void incrementSuccess() { success++; }
    public void incrementDuplicated() { duplicatedEmails++; }
    public void incrementInvalid() { invalidRows++; }

    public int getTotal() { return total; }
    public int getSuccess() { return success; }
    public int getDuplicatedEmails() { return duplicatedEmails; }
    public int getInvalidRows() { return invalidRows; }
    public List<String> getErrors() { return errors; }

    public void addError(String error) {
        errors.add(error);
    }
}
