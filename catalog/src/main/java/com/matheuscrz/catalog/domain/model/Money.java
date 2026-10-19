package com.matheuscrz.catalog.domain.model;

import com.matheuscrz.catalog.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) implements Comparable<Money> {

    public static final Currency BRL = Currency.getInstance("BRL");

    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Amount cannot be negative");
        }
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Money ofBrl(BigDecimal amount) {
        return new Money(amount, BRL);
    }

    public static Money ofBrl(String amount) {
        return new Money(new BigDecimal(amount), BRL);
    }

    public static Money zeroBrl() {
        return new Money(BigDecimal.ZERO, BRL);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        BigDecimal newAmount = this.amount.subtract(other.amount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Resulting money cannot be negative");
        }
        return new Money(newAmount, this.currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new DomainException("Factor cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money operand cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new DomainException(
                    "Cannot operate on different currencies: " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public int compareTo(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }
}