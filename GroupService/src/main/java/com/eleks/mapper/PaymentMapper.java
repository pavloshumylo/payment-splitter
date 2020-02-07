package com.eleks.mapper;

import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;
import com.eleks.entity.PaymentEntity;
import com.eleks.exception.InvalidRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PaymentMapper {

    public static PaymentEntity convertToEntity(PaymentRequestDto paymentRequestDto, Long groupId, Long creatorId) {
        if (Objects.isNull(paymentRequestDto)) {
            throw new InvalidRequestException("Payment request dto can not be null");
        }

        return PaymentEntity.builder()
                .groupId(groupId)
                .paymentDescription(paymentRequestDto.getPaymentDescription())
                .price(paymentRequestDto.getPrice())
                .coPayers(paymentRequestDto.getCoPayers())
                .creatorId(creatorId)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static List<PaymentResponseDto> convertToDto(List<PaymentEntity> paymentEntities) {
        if (Objects.isNull(paymentEntities) || paymentEntities.isEmpty()) {
            throw new InvalidRequestException("Payment entities list can not be null or empty");
        }

        return paymentEntities.stream()
                .map(paymentEntity -> new PaymentResponseDto(paymentEntity.getId(), paymentEntity.getGroupId(), paymentEntity.getPaymentDescription(),
                        paymentEntity.getPrice(), paymentEntity.getCoPayers(), paymentEntity.getCreatorId(), paymentEntity.getTimeStamp()))
                .collect(Collectors.toList());
    }
}
