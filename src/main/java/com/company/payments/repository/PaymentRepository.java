package com.company.payments.repository;

import com.company.payments.data.response.PaymentResponse;
import com.company.payments.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByExternalId(String externalId);

    List<PaymentEntity> findAllByEmail(String email);


}
