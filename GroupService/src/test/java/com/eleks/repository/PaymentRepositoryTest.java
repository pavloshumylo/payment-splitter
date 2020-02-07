package com.eleks.repository;

import com.eleks.entity.GroupEntity;
import com.eleks.entity.PaymentEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private PaymentEntity firstPaymentEntityExpected, secondPaymentEntityExpected;

    @Before
    public void init() {
        firstPaymentEntityExpected = PaymentEntity.builder()
                .paymentDescription("testPaymentDescription")
                .price(12.3)
                .coPayers(Arrays.asList(1L, 2L))
                .creatorId(1L)
                .timeStamp(LocalDateTime.of(2012, 1, 1, 10, 15, 3))
                .build();

        secondPaymentEntityExpected = PaymentEntity.builder()
                .paymentDescription("testPaymentDescription")
                .price(12.3)
                .coPayers(Arrays.asList(1L, 2L))
                .creatorId(1L)
                .timeStamp(LocalDateTime.of(2012, 1, 1, 10, 15, 3))
                .build();
    }

    @Test
    public void findById_paymentExist_shouldReturnProperPaymentById() {
        entityManager.persist(firstPaymentEntityExpected);
        entityManager.persist(secondPaymentEntityExpected);

        Optional<PaymentEntity> paymentEntityActual = paymentRepository.findById(firstPaymentEntityExpected.getId());

        assertTrue(paymentEntityActual.isPresent());
        assertEquals(firstPaymentEntityExpected, paymentEntityActual.get());
    }

    @Test
    public void findById_paymentDoesNotExist_shouldReturnEmptyOptional() {
        assertFalse(paymentRepository.findById(1L).isPresent());
    }

    @Test
    public void findByGroupId_paymentWithGroupIdExist_shouldReturnListOfProperPayments() {
        GroupEntity groupEntity = GroupEntity.builder()
                .groupName("testGroupNameFirst")
                .currency("testCurrency")
                .build();

        entityManager.persist(groupEntity);

        firstPaymentEntityExpected.setGroupId(groupEntity.getId());

        entityManager.persist(firstPaymentEntityExpected);
        entityManager.persist(secondPaymentEntityExpected);

        assertEquals(Collections.singletonList(firstPaymentEntityExpected), paymentRepository.findByGroupId(groupEntity.getId()));
    }

    @Test
    public void findByGroupId_paymentWithGroupIdDoNotExist_shouldReturnListOfProperPayments() {
        entityManager.persist(firstPaymentEntityExpected);
        entityManager.persist(secondPaymentEntityExpected);

        assertTrue(paymentRepository.findByGroupId(77L).isEmpty());
    }

    @Test
    public void delete_paymentExistsBeforeDeletion_shouldDeleteProperPayment() {
        entityManager.persist(secondPaymentEntityExpected);

        paymentRepository.delete(entityManager.persist(firstPaymentEntityExpected));
        paymentRepository.delete(secondPaymentEntityExpected.getId());

        assertTrue(paymentRepository.findAll().isEmpty());
    }

    @Test
    public void getOne_paymentExists_shouldReturnProperPayment() {
        entityManager.persist(firstPaymentEntityExpected);
        entityManager.persist(secondPaymentEntityExpected);

        assertEquals(secondPaymentEntityExpected, paymentRepository.getOne(secondPaymentEntityExpected.getId()));
    }

    @Test
    public void save_shouldSaveAndReturnExpectedPayment() {
        PaymentEntity firstPaymentEntityActual = paymentRepository.save(firstPaymentEntityExpected);
        PaymentEntity secondPaymentEntityActual = paymentRepository.save(secondPaymentEntityExpected);

        assertEquals(firstPaymentEntityExpected, firstPaymentEntityActual);
        assertEquals(secondPaymentEntityExpected, secondPaymentEntityActual);
        assertEquals(2, paymentRepository.count());
    }
}
