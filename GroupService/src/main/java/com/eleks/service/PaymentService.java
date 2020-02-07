package com.eleks.service;

import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {

    void createPayment(PaymentRequestDto paymentRequestDto, Long groupId);

    List<PaymentResponseDto> retrievePayments(Long groupId);

    PaymentResponseDto retrievePayment(Long groupId, Long paymentId);

    void deletePayment(Long groupId, Long paymentId);
}
