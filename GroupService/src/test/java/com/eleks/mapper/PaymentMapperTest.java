package com.eleks.mapper;

import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;
import com.eleks.entity.PaymentEntity;
import com.eleks.exception.InvalidRequestException;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PaymentMapperTest {

    @Test
    public void convertToEntity_paymentRequestDtoNotNull_shouldReturnProperPaymentEntity() {
        PaymentRequestDto paymentRequestDto =
                new PaymentRequestDto("testPaymentDescription", 12.3, Arrays.asList(1L, 2L));

        PaymentEntity paymentEntityExpected = PaymentEntity.builder()
                .groupId(2L)
                .paymentDescription("testPaymentDescription")
                .price(12.3)
                .coPayers(Arrays.asList(1L, 2L))
                .creatorId(1L)
                .timeStamp(LocalDateTime.of(2012, 1, 1, 10, 15, 3))
                .build();

        PaymentEntity paymentEntityActual = PaymentMapper.convertToEntity(paymentRequestDto, 2L, 1L);

        assertThat(paymentEntityActual).isEqualToIgnoringGivenFields(paymentEntityExpected, "timeStamp");
        assertNotNull(paymentEntityActual.getTimeStamp());
    }

    @Test
    public void convertToEntity_paymentRequestDtoIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> PaymentMapper.convertToEntity(null, 2L, 1L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Payment request dto can not be null");
    }

    @Test
    public void convertToDto_paymentEntityListNotNull_shouldReturnProperPaymentResponseDtoList() {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .groupId(2L)
                .paymentDescription("testPaymentDescription")
                .price(12.3)
                .coPayers(Arrays.asList(1L, 2L))
                .creatorId(1L)
                .timeStamp(LocalDateTime.of(2012, 1, 1, 10, 15, 3))
                .build();

        PaymentResponseDto paymentResponseDtoExpected =
                new PaymentResponseDto(paymentEntity.getId(), paymentEntity.getGroupId(), paymentEntity.getPaymentDescription(),
                        paymentEntity.getPrice(), paymentEntity.getCoPayers(), paymentEntity.getCreatorId(), paymentEntity.getTimeStamp());

        assertEquals(Collections.singletonList(paymentResponseDtoExpected), PaymentMapper.convertToDto(Collections.singletonList(paymentEntity)));
    }

    @Test
    public void convertToDto_paymentEntityListIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> PaymentMapper.convertToDto(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Payment entities list can not be null or empty");
    }

    @Test
    public void convertToDto_paymentEntityListIsEmpty_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> PaymentMapper.convertToDto(Collections.emptyList()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Payment entities list can not be null or empty");
    }
}
