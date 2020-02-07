package com.eleks.service;

import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;
import com.eleks.dto.UserPrincipal;
import com.eleks.entity.GroupEntity;
import com.eleks.entity.PaymentEntity;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.repository.GroupRepository;
import com.eleks.repository.PaymentRepository;
import com.eleks.security.AuthenticationPrincipalSecurityUtil;
import com.eleks.service.impl.PaymentServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepositoryMock;

    @Mock
    private GroupRepository groupRepositoryMock;

    @Mock
    private AuthenticationPrincipalSecurityUtil authenticationSecurityUtil;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequestDto paymentRequestDtoExpected;

    private PaymentResponseDto paymentResponseDtoExpected;

    private PaymentEntity paymentEntityExpected;

    private GroupEntity groupEntityExpected;

    @Before
    public void init() {
        paymentRequestDtoExpected =
                new PaymentRequestDto("paymentDescriptionTest", 12.3, Arrays.asList(1L, 2L));

        paymentResponseDtoExpected = new PaymentResponseDto(1L, 3L, "paymentDescriptionTest", 12.3, Arrays.asList(1L, 2L), 4L,
                LocalDateTime.of(2012, 1, 1, 10, 15, 3));

        groupEntityExpected = GroupEntity.builder()
                .id(1L)
                .groupName("groupNameTest")
                .currency("USD")
                .members(Arrays.asList(UserEntity.builder().userId(2L).build(), UserEntity.builder().userId(3L).build()))
                .build();

        paymentEntityExpected = PaymentEntity.builder()
                .id(1L)
                .groupId(3L)
                .paymentDescription("paymentDescriptionTest")
                .price(12.3)
                .coPayers(Arrays.asList(1L, 2L))
                .creatorId(4L)
                .timeStamp(LocalDateTime.of(2012, 1, 1, 10, 15, 3))
                .build();
    }

    @Test
    public void createPayment_groupWithProperGroupIdDoesNotExist_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPayment(paymentRequestDtoExpected, 3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group with 3 group id doesn't exist");
    }

    @Test
    public void createPayment_groupMembersListDoNotContainAllCoPayersIds_shouldThrowInvalidRequestException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        assertThatThrownBy(() -> paymentService.createPayment(paymentRequestDtoExpected, 3L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Co-payers ids are not members of group with id 3");
    }

    @Test
    public void createPayment_coPayersIdsValid_shouldInvokeRepositoryMethodSaveOnce() {
        groupEntityExpected.getMembers().get(1).setUserId(1L);
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));
        when(authenticationSecurityUtil.retrievePrincipal()).thenReturn(new UserPrincipal("testName", 4L));

        paymentService.createPayment(paymentRequestDtoExpected, 3L);

        verify(paymentRepositoryMock).save(any(PaymentEntity.class));
    }

    @Test
    public void createPayment_userPrincipalIsNull_shouldThrowInvalidRequestException() {
        groupEntityExpected.getMembers().get(1).setUserId(1L);
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        assertThatThrownBy(() -> paymentService.createPayment(paymentRequestDtoExpected, 3L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Authentication principal can not be null");
    }

    @Test
    public void retrievePayments_paymentsWithGroupIdDoNotExist_shouldThrowResourceNotFoundException() {
        when(paymentRepositoryMock.findByGroupId(3L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> paymentService.retrievePayments(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payments with group id 3 doesn't exist");
    }

    @Test
    public void retrievePayments_paymentsWithGroupIdExist_shouldReturnProperListOfPaymentResponseDto() {
        when(paymentRepositoryMock.findByGroupId(3L)).thenReturn(Collections.singletonList(paymentEntityExpected));

        assertEquals(Collections.singletonList(paymentResponseDtoExpected), paymentService.retrievePayments(3L));
    }

    @Test
    public void retrievePayment_groupWithProperGroupIdDoesNotExist_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.retrievePayment(3L, 4L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group with 3 group id doesn't exist");
    }

    @Test
    public void retrievePayment_paymentWithProperPaymentIdDoesNotExist_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        assertThatThrownBy(() -> paymentService.retrievePayment(3L, 4L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment with 4 payment id doesn't exist");
    }

    @Test
    public void retrievePayment_paymentExist_shouldReturnProperPaymentResponseDto() {
        groupEntityExpected.setPayments(Collections.singletonList(paymentEntityExpected));
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        assertEquals(paymentResponseDtoExpected, paymentService.retrievePayment(3L, 1L));
    }

    @Test
    public void deletePayment_groupWithProperGroupIdDoesNotExist_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.deletePayment(3L, 4L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group with 3 group id doesn't exist");
    }

    @Test
    public void deletePayment_paymentWithProperPaymentIdDoesNotExist_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        assertThatThrownBy(() -> paymentService.deletePayment(3L, 4L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment with 4 payment id doesn't exist");
    }

    @Test
    public void deletePayment_paymentExist_shouldInvokeRepositoryMethodDeleteOnce() {
        groupEntityExpected.setPayments(Collections.singletonList(paymentEntityExpected));
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        paymentService.deletePayment(3L, 1L);

        verify(paymentRepositoryMock).delete(paymentEntityExpected);
    }
}
