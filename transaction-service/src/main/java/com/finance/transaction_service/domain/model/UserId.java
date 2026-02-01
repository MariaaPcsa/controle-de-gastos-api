package com.finance.transaction_service.domain.model;

import java.util.Objects;

public class UserId {

    private final Long value;

    public UserId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId não pode ser nulo");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
