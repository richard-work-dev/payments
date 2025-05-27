package com.company.payments.controller;

import com.company.payments.constant.ErrorsMessageEnum;
import com.company.payments.data.request.PaymentRequest;
import com.company.payments.data.response.PaymentResponse;
import com.company.payments.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {
        PaymentRequest request = buildValidRequest();

        PaymentResponse response = new PaymentResponse();
        response.setExternalId("1234567890");
        response.setCurrency("PEN");
        response.setAmount(new BigDecimal("20.0"));
        response.setEmail("test@gmail.com");

        when(paymentService.savePayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/payments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.external_id").value("1234567890"));
    }

    @Test
    void shouldReturnConflictIfExternalIdExists() throws Exception {
        PaymentRequest request = buildValidRequest();

        when(paymentService.savePayment(any(PaymentRequest.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate external_id"));

        mockMvc.perform(post("/payments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error_code").value(ErrorsMessageEnum.PAYMENT_DUPLICATE_EXTERNAL_ID.getCode()));
    }

    @Test
    void shouldGetPaymentByExternalIdSuccessfully() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setExternalId("1234567890");
        response.setCurrency("PEN");
        response.setAmount(new BigDecimal("20.0"));
        response.setEmail("test@gmail.com");

        when(paymentService.getPaymentByExternalId("1234567890")).thenReturn(Optional.of(response));

        mockMvc.perform(get("/payments/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.external_id").value("1234567890"));
    }

    @Test
    void shouldReturnNotFoundForUnknownExternalId() throws Exception {
        when(paymentService.getPaymentByExternalId("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/payments/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value(ErrorsMessageEnum.PAYMENT_NOT_FOUND.getCode()));
    }

    @Test
    void shouldGetPaymentsByEmail() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setExternalId("1234567890");
        response.setCurrency("PEN");
        response.setAmount(new BigDecimal("20.0"));
        response.setEmail("test@gmail.com");

        when(paymentService.getPaymentByEmail("test@gmail.com")).thenReturn(List.of(response));

        mockMvc.perform(get("/payments/email/test@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].external_id").value("1234567890"));
    }

    private PaymentRequest buildValidRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setExternalId("1234567890");
        request.setCurrency("PEN");
        request.setAmount("20.0");
        request.setEmail("test@gmail.com");
        return request;
    }
}
