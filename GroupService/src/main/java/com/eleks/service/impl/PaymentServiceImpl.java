package com.eleks.service.impl;

import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;
import com.eleks.dto.UserPrincipal;
import com.eleks.entity.GroupEntity;
import com.eleks.entity.PaymentEntity;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.mapper.PaymentMapper;
import com.eleks.repository.GroupRepository;
import com.eleks.repository.PaymentRepository;
import com.eleks.security.AuthenticationPrincipalSecurityUtil;
import com.eleks.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    // should be removed in 6 iteration
    private static final Long CREATOR_ID = 4L;

    private GroupRepository groupRepository;

    private PaymentRepository paymentRepository;

    private AuthenticationPrincipalSecurityUtil authenticationSecurityUtil;

    @Autowired
    public PaymentServiceImpl(GroupRepository groupRepository, PaymentRepository paymentRepository, AuthenticationPrincipalSecurityUtil authenticationSecurityUtil) {
        this.groupRepository = groupRepository;
        this.paymentRepository = paymentRepository;
        this.authenticationSecurityUtil = authenticationSecurityUtil;
    }

    @Transactional
    public void createPayment(PaymentRequestDto paymentRequestDto, Long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with " + groupId + " group id doesn't exist"));

        List<Long> groupMembers = groupEntity.getMembers().stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toList());

        if (groupMembers.containsAll(paymentRequestDto.getCoPayers())) {

            UserPrincipal userPrincipal = authenticationSecurityUtil.retrievePrincipal();

            if (Objects.isNull(userPrincipal)) {
                throw new InvalidRequestException("Authentication principal can not be null");
            }

            paymentRepository.save(PaymentMapper.convertToEntity(paymentRequestDto, groupId, userPrincipal.getUserId()));

        } else {
            throw new InvalidRequestException("Co-payers ids are not members of group with id " + groupId);
        }
    }

    @Override
    public List<PaymentResponseDto> retrievePayments(Long groupId) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByGroupId(groupId);

        if (paymentEntities.isEmpty()) {
            throw new ResourceNotFoundException("Payments with group id " + groupId + " doesn't exist");
        }

        return PaymentMapper.convertToDto(paymentEntities);
    }

    @Override
    public PaymentResponseDto retrievePayment(Long groupId, Long paymentId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with " + groupId + " group id doesn't exist"));

        PaymentEntity paymentEntity = groupEntity.getPayments().stream()
                .filter(payment -> payment.getId().equals(paymentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Payment with " + paymentId + " payment id doesn't exist"));

        return PaymentMapper.convertToDto(Collections.singletonList(paymentEntity)).get(0);
    }

    @Transactional
    public void deletePayment(Long groupId, Long paymentId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with " + groupId + " group id doesn't exist"));

        PaymentEntity paymentEntity = groupEntity.getPayments().stream()
                .filter(payment -> payment.getId().equals(paymentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Payment with " + paymentId + " payment id doesn't exist"));

        paymentRepository.delete(paymentEntity);
    }
}
