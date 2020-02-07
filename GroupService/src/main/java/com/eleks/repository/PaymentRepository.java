package com.eleks.repository;

import com.eleks.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findByGroupId(Long groupId);

    Optional<PaymentEntity> findById(Long id);
}
