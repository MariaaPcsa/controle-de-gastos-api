package com.maria.finance.user.infrastructure.excel;

import java.util.ArrayList;
import java.util.List;

public class UserImportResult {

    private int total;
    private int success;
    private int duplicatedEmails;
    private int invalidRows;

    private List<UserImportError> errors = new ArrayList<>();

    public void incrementTotal() { total++; }
    public void incrementSuccess() { success++; }
    public void incrementDuplicated() { duplicatedEmails++; }
    public void incrementInvalid() { invalidRows++; }

    public int getTotal() { return total; }
    public int getSuccess() { return success; }
    public int getDuplicatedEmails() { return duplicatedEmails; }
    public int getInvalidRows() { return invalidRows; }
    public List<UserImportError> getErrors() { return errors; }

    public void addError(UserImportError error) {
        errors.add(error);
    }
}