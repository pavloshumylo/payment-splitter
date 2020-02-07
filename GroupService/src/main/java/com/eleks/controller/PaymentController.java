package com.eleks.controller;

import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;
import com.eleks.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/groups/{groupId}/payments")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    ResponseEntity<Void> createPayment(@PathVariable Long groupId, @RequestBody @Valid PaymentRequestDto paymentRequestDto) {
        paymentService.createPayment(paymentRequestDto, groupId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    List<PaymentResponseDto> retrievePayments(@PathVariable Long groupId) {
        return paymentService.retrievePayments(groupId);
    }

    @GetMapping(value = "{paymentId}")
    PaymentResponseDto retrivePayment(@PathVariable Long groupId, @PathVariable Long paymentId) {
        return paymentService.retrievePayment(groupId, paymentId);
    }

    @DeleteMapping(value = "{paymentId}")
    ResponseEntity<Void> deletePayment(@PathVariable Long groupId, @PathVariable Long paymentId) {
        paymentService.deletePayment(groupId, paymentId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
