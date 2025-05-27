package com.company.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorsMessageEnum {
    PAYMENT_NOT_FOUND("Payment not found: {}"),
    PAYMENT_DUPLICATE_EXTERNAL_ID("Payment with external_id already exists: {}"),
    PAYMENT_CREATED_ERROR("Error creating payment"),
    PAYMENT_GET_ERROR("Error getting payment: {}");


    private final String message;

    // Constructor
    ErrorsMessageEnum(String message) {
        this.message = message;
    }

    public String getCode() {
        return name();
    }
}
