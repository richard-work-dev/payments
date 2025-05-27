package com.company.payments.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponse {

    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("created_at")
    private String createdAt;

}
