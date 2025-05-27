package com.company.payments.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentRequest {

    @Size(max = 100, message = "external_id must be less than 100 characters")
    @NotEmpty(message = "external_id is required")
    @JsonProperty("external_id")
    private String externalId;

    @Size(max = 100, message = "email must be less than 100 characters")
    @NotEmpty(message = "email is required")
    @JsonProperty("email")
    private String email;

    @NotEmpty(message = "amount is required")
    @JsonProperty("amount")
    private String amount;

    @NotEmpty(message = "currency is required")
    @Pattern(regexp = "^(PEN|USD)$", message = "currency must be PEN or USD")
    @JsonProperty("currency")
    private String currency;

}
