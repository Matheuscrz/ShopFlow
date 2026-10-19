package com.matheuscrz.catalog.domain.exception;

public class ProductInactiveException extends DomainException {
    public ProductInactiveException(String message) {
        super(message);
    }
}