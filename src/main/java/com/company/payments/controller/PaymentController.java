package com.company.payments.controller;

import com.company.payments.data.request.PaymentRequest;
import com.company.payments.data.response.ErrorResponse;
import com.company.payments.data.response.PaymentResponse;
import com.company.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin("*") // Allow all origins – useful in dev/test, but consider restricting in production
@RequestMapping(value = "/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor // Automatically generates constructor injection for final fields
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Creates a new payment.
     * @return 201 Created on success,
     *         409 Conflict if a duplicate external_id is detected,
     *         500 Internal Server Error for any unexpected failure.
     */
    @PostMapping("/create")
    public ResponseEntity createPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            log.info("Creating payment - request: {}", request);
            return ResponseEntity.status(201).body(paymentService.savePayment(request));

        } catch (DataIntegrityViolationException e) {
            // Likely caused by a unique constraint violation on external_id
            log.error("Duplicate external_id detected - request: {}", request, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorMessage("Payment with external_id " + request.getExternalId() + " already exists");
            errorResponse.setErrorCode("ERR-01");
            return ResponseEntity.status(409).body(errorResponse);

        } catch (Exception e) {
            // Catch-all for any unexpected errors
            log.error("Unexpected error while creating payment: {}", request, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorMessage("Error saving payment: " + e.getMessage());
            errorResponse.setErrorCode("ERR-02");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Retrieves a payment by external_id.
     *
     * @return 200 OK with the payment if found,
     *         404 Not Found if the payment does not exist,
     *         500 Internal Server Error on failure.
     */
    @GetMapping("/{externalId}")
    public ResponseEntity getPayment(@Valid @PathVariable String externalId) {
        try {
            log.info("Fetching payment with externalId: {}", externalId);
            Optional<PaymentResponse> paymentResponse = paymentService.getPaymentByExternalId(externalId);

            if (paymentResponse.isEmpty()) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setErrorMessage("Payment with external_id " + externalId + " not found");
                errorResponse.setErrorCode("ERR-03");
                return ResponseEntity.status(404).body(errorResponse);
            }

            return ResponseEntity.ok(paymentResponse.get());

        } catch (Exception e) {
            log.error("Error retrieving payment: {}", externalId, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorMessage("Error getting payment: " + e.getMessage());
            errorResponse.setErrorCode("ERR-04");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Retrieves payments associated with an email address.
     * @return 200 OK with a list of payments (may be empty).
     */
    @GetMapping("/email/{email}")
    public ResponseEntity getPaymentByEmail(@Valid @PathVariable String email) {
        try {
            log.info("Fetching payments for email: {}", email);
            return ResponseEntity.ok(paymentService.getPaymentByEmail(email));
        } catch (Exception e) {
            log.error("Error retrieving payments for email: {}", email, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorMessage("Error getting payments: " + e.getMessage());
            errorResponse.setErrorCode("ERR-05");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
