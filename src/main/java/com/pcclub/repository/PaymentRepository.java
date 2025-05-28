// src/main/java/com/pcclub/repository/PaymentRepository.java
package com.pcclub.repository;

import com.pcclub.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
