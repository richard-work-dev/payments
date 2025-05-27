package com.company.payments.service;

import com.company.payments.data.request.PaymentRequest;
import com.company.payments.data.response.PaymentResponse;
import com.company.payments.entity.PaymentEntity;
import com.company.payments.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {

    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse savePayment(PaymentRequest paymentRequest) {
        try {
            log.info("Saving payment: {}", paymentRequest);
            PaymentEntity paymentEntity = new PaymentEntity();
            paymentEntity.setExternalId(paymentRequest.getExternalId());
            paymentEntity.setCurrency(paymentRequest.getCurrency());
            paymentEntity.setAmount(new BigDecimal(paymentRequest.getAmount()));
            paymentEntity.setEmail(paymentRequest.getEmail());

            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);
            log.info("Saved payment: {}", savedPayment);
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setExternalId(savedPayment.getExternalId());
            paymentResponse.setAmount(savedPayment.getAmount());
            paymentResponse.setCurrency(savedPayment.getCurrency());
            paymentResponse.setEmail(savedPayment.getEmail());
            paymentResponse.setCreatedAt(String.valueOf(savedPayment.getCreatedAt()));
            return paymentResponse;

        } catch (DataIntegrityViolationException e) {
            log.error("Error saving payment: {}", paymentRequest, e);
            throw e;
        }
    }

    public Optional<PaymentResponse> getPaymentByExternalId(String externalId) {
        try {
            log.info("Getting payment: {}", externalId);
            Optional<PaymentEntity> paymentEntity = paymentRepository.findByExternalId(externalId);
            if (paymentEntity.isPresent()) {
                PaymentResponse paymentResponse = new PaymentResponse();
                paymentResponse.setExternalId(paymentEntity.get().getExternalId());
                paymentResponse.setAmount(paymentEntity.get().getAmount());
                paymentResponse.setCurrency(paymentEntity.get().getCurrency());
                paymentResponse.setEmail(paymentEntity.get().getEmail());
                paymentResponse.setCreatedAt(String.valueOf(paymentEntity.get().getCreatedAt()));
                return Optional.of(paymentResponse);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting payment: {}", externalId, e);
            return Optional.empty();
        }
    }

    public List<PaymentResponse> getPaymentByEmail(String email) {
        try {
            log.info("Getting payment: {}", email);
            List<PaymentEntity> paymentEntities = paymentRepository.findAllByEmail(email);
            return paymentEntities.stream().map(paymentEntity -> {
                PaymentResponse paymentResponse = new PaymentResponse();
                paymentResponse.setExternalId(paymentEntity.getExternalId());
                paymentResponse.setAmount(paymentEntity.getAmount());
                paymentResponse.setCurrency(paymentEntity.getCurrency());
                paymentResponse.setEmail(paymentEntity.getEmail());
                paymentResponse.setCreatedAt(String.valueOf(paymentEntity.getCreatedAt()));
                return paymentResponse;
            }).toList();
        } catch (Exception e) {
            log.error("Error getting payment: {}", email, e);
            return List.of();
        }

    }

}
